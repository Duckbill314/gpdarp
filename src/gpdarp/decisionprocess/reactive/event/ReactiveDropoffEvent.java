package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Request;
import gpdarp.core.Node;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

/**
 * This event represents arrival of a vehicle to a dropoff point in its route.
 * During this event, the vehicle may potentially accept a request from the waiting list.
 * Then, the node is visited, the vehicle state is updated,
 * and a new event is invoked to move to the next point in the route.
 * A dropoff point implies that the vehicle's route may end.
 * If it does, the vehicle may either go to charge, or remain where it is.
 *
 * @author William Huang
 */
public class ReactiveDropoffEvent extends DecisionProcessEvent {
    Node node;
    Vehicle vehicle;

    public ReactiveDropoffEvent(int time, Node node, Vehicle vehicle) {
        super("ReactiveDropoffEvent", time);
        this.node = node;
        this.vehicle = vehicle;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        Node waitingPoint = node.idleClone(time + vehicle.getServeTime());

        boolean includeStations = vehicle.getPlannedRoute().isEmpty();
        Request request = requestAllocation(decisionProcess, vehicle, waitingPoint, includeStations);

        vehicle.dropoff(node);

        Node destination = vehicle.updateArcFromPlannedRoute();

        if (destination != null) {
            switch (destination.getType()) {
                case PICKUP -> decisionProcess.addEvent(
                        new ReactivePickupEvent(destination.getArrivalTime(), destination, vehicle));

                case DROPOFF -> decisionProcess.addEvent(
                        new ReactiveDropoffEvent(destination.getArrivalTime(), destination, vehicle));
            }
        }
        else {
            vehicle.setCurrPos(waitingPoint);
            if (request != null) {
                vehicle.charge(decisionProcess.getState().getInstance(), request);
                decisionProcess.addEvent(new ReactiveChargeEvent(vehicle.getCurrPos().getArrivalTime(), vehicle));
            }
        }
    }
}
