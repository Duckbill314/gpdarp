package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Request;
import gpdarp.core.Node;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

/**
 * This event represents arrival of a vehicle to a dropoff point in its route.
 * During this event, the node is visited, the vehicle state is updated,
 * and a new event is invoked to move to the next point in the route.
 * A dropoff point implies that the vehicle's route may be ending.
 * If it does, the vehicle may take a new route, go to charge, or remain where it is.
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
        vehicle.dropoff(node);

        if (vehicle.getPlannedRoute().isEmpty()) {
            vehicle.setCurrPos(node);
            Request request = requestAllocation(decisionProcess, vehicle, true, false);

            if (request != null) {
                if (request.getType() != Request.RequestType.CHARGE) {
                    constructiveHeuristic(decisionProcess, vehicle, false);
                }
            }
        }

        Node destination = vehicle.updateArcFromPlannedRoute();

        if (destination != null) {
            switch (destination.getType()) {
                case PICKUP -> decisionProcess.addEvent(
                        new ReactivePickupEvent(destination.getArrivalTime(), destination, vehicle));

                case DROPOFF -> decisionProcess.addEvent(
                        new ReactiveDropoffEvent(destination.getArrivalTime(), destination, vehicle));

                case STATION -> {
                    vehicle.charge();
                    decisionProcess.addEvent(new ReactiveChargeEvent(vehicle.getCurrPos().getDepartureTime(), vehicle));
                }
            }
        }
        else {
            vehicle.setCurrPos(node);
        }
    }
}
