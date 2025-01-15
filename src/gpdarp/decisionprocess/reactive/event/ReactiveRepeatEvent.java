package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

/**
 * This event is invoked when an incoming request is not accepted by any vehicle due to the fact that its
 * pickup time is too far in the future. It gives the request another chance to be accepted by a vehicle closer
 * to the request's pickup time.
 *
 * @author William Huang
 */
public class ReactiveRepeatEvent extends DecisionProcessEvent {
    Request request;

    public ReactiveRepeatEvent(int time, Request request) {
        super("ReactiveRepeatEvent", time);
        this.request = request;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        if (decisionProcess.getWaitingList().contains(request)) {
            Vehicle vehicle = vehicleAllocation(decisionProcess, request);

            if (vehicle != null) {
                constructiveHeuristic(decisionProcess, vehicle);
                vehicle.setCurrPos(null);
                Node destination = vehicle.updateArcFromPlannedRoute();
                decisionProcess.addEvent(new ReactivePickupEvent(destination.getArrivalTime(), destination, vehicle));
            }
        }
    }
}
