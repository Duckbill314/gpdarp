package gpdarp.decisionprocess;

import gpdarp.core.IdleRequest;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.representation.route.Route;

import java.util.List;
import java.util.Map;

/**
 * An abstract decision process event.
 * It has a time that the event occurs, with natural comparison preferring the earlier event.
 *
 * @author gphhucarp, William Huang
 */
public abstract class DecisionProcessEvent implements Comparable<DecisionProcessEvent> {
    protected int time;

    public DecisionProcessEvent(int time) {
        this.time = time;
    }

    // Getters
    public int getTime() {
        return time;
    }

    /**
     * Execute the event from within the context of the decision process.
     *
     * @param decisionProcess the decision process that invoked this event.
     */
    public abstract void trigger(DecisionProcess decisionProcess);

    @Override
    public int compareTo(DecisionProcessEvent o) { return Integer.compare(time, o.time); }

    /**
     * Helper method for handling vehicle allocation.
     *
     * @param decisionProcess the decision process that invoked this event.
     * @param request the request to be allocated.
     *
     * @return the vehicle to which the request is allocated.
     */
    public Vehicle vehicleAllocation(DecisionProcess decisionProcess, Request request) {
        DecisionProcessState state = decisionProcess.getState();
        Map.Entry<Vehicle, Route> allocation = decisionProcess.getVehiclePolicy().next(state, request);

        if (allocation == null) {
            decisionProcess.addWaiting((IdleRequest) request);
            return null;
        }

        Vehicle vehicle = allocation.getKey();
        Route route = allocation.getValue();
        vehicle.allocate(request);
        vehicle.updateRoute(state, route);
        return vehicle;
    }

    /**
     * Helper method for handling waiting list request allocation.
     *
     * @param decisionProcess the decision process that invoked this event.
     * @param vehicle the vehicle to which a request is to be allocated.
     */
    public void requestAllocation(DecisionProcess decisionProcess, Vehicle vehicle) {
        DecisionProcessState state = decisionProcess.getState();
        List<IdleRequest> waitingList = decisionProcess.getWaitingList();
        Map.Entry<Request, Route> allocation = decisionProcess.getRequestPolicy().next(vehicle, state, waitingList);

        if (allocation != null) {
            Request request = allocation.getKey();
            Route route = allocation.getValue();
            vehicle.allocate(request);
            vehicle.updateRoute(state, route);
            waitingList.remove(request);
        }
    }
}
