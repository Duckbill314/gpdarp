package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

/**
 * This event represents the moment right after a vehicle has finished charging.
 * It will be given one final opportunity to accept a waiting request.
 * Then, a new event is invoked to move to the next point in the route.
 * If it does not move, it will wait where it is until it receives its next fresh request.
 *
 * @author William Huang
 */
public class ReactiveChargeEvent extends DecisionProcessEvent {
    Vehicle vehicle;

    public ReactiveChargeEvent(int time, Vehicle vehicle) {
        super("ReactiveChargeEvent", time);
        this.vehicle = vehicle;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        Request request = requestAllocation(decisionProcess, vehicle, false);
        if (request != null) {
            constructiveHeuristic(decisionProcess, vehicle);
        }

        Node destination = vehicle.updateArcFromPlannedRoute();

        if (destination != null) {
            vehicle.setCurrPos(null);
            decisionProcess.addEvent(new ReactivePickupEvent(destination.getArrivalTime(), destination, vehicle));
        }
    }
}
