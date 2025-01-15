package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

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
