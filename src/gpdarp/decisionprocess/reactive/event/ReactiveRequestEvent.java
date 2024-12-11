package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Arc;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.reactive.ReactiveDecisionSituation;

import java.util.List;

public class ReactiveRequestEvent extends DecisionProcessEvent {
    Request request;

    public ReactiveRequestEvent(int time, Request request) {
        super(time);
        this.request = request;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        // Build a reactive decision situation
        DecisionProcessState state = decisionProcess.getState();
        List<Vehicle> vehicles = state.getInstance().getVehicles();
        ReactiveDecisionSituation rds = new ReactiveDecisionSituation(vehicles, state);

        // Use the reactive decision situation in the allocation policy to find the vehicle allocation
        Vehicle allocation = decisionProcess.getAllocationPolicy().next(rds, request);

        // Recalculate the planned route with the inclusion of the new request
        List<Request> requests = allocation.getRequests();
        requests.add(request);
        allocation.setPlannedRoute(allocation.recalculate(requests));

        // Invoke movement if the vehicle is not currently moving
        if (allocation.getCurrArc() == null) {
            allocation.setCurrPos(null);
            Arc currArc = allocation.updateArcFromPlannedRoute();
            int timeTaken = state.getInstance().calculateTravelTime(currArc.length());
            int time = this.getTime() + timeTaken;
            Node destination = currArc.to();
            decisionProcess.getEventQueue().add(new ReactivePickupEvent(time, destination));
            destination.setEta(time);
        }
    }
}
