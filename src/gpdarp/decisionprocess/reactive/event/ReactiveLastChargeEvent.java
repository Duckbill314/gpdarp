package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

/**
 * This event is for forcefully charging vehicles during last call.
 * That way, charge constraint violation should no longer be an inhibiting factor for allocation.
 *
 * @author William Huang
 */
public class ReactiveLastChargeEvent extends DecisionProcessEvent {
    Vehicle vehicle;

    public ReactiveLastChargeEvent(int time, Vehicle vehicle) {
        super("ReactiveLastChargeEvent", time);
        this.vehicle = vehicle;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        Request request = requestAllocation(decisionProcess, vehicle, false, true);
        if (request != null) {
            constructiveHeuristic(decisionProcess, vehicle, true);
        }

        Node destination = vehicle.updateArcFromPlannedRoute();

        if (destination != null) {
            vehicle.setCurrPos(null);
            decisionProcess.addEvent(new ReactivePickupEvent(destination.getArrivalTime(), destination, vehicle));
        }
    }
}
