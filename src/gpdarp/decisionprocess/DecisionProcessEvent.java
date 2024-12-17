package gpdarp.decisionprocess;

import gpdarp.core.WaitingRequest;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.representation.route.Route;

import java.util.ArrayList;
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
            decisionProcess.addWaiting(new WaitingRequest(request));
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
     * @param vehicle         the vehicle to which a request is to be allocated.
     * @param waitingPoint    the point and time at which the vehicle will be after finishing its current task.
     * @param includeStations whether to also consider returning to a charging station.
     *
     * @return the request allocation.
     */
    public WaitingRequest requestAllocation(DecisionProcess decisionProcess, Vehicle vehicle, Node waitingPoint,
                                            boolean includeStations) {

        DecisionProcessState state = decisionProcess.getState();
        List<WaitingRequest> waitingList = new ArrayList<>(decisionProcess.getWaitingList());

        if (includeStations) {
            state.getInstance().getStations().forEach(s -> waitingList.add(
                    new WaitingRequest(waitingPoint.getEta(), waitingPoint, s.clone())));
        }
        Map.Entry<WaitingRequest, Route> allocation = decisionProcess.getRequestPolicy().next(vehicle, state, waitingList);

        if (allocation == null) {
            return null;
        }

        WaitingRequest request = allocation.getKey();
        if (request.isRequest) {
            Route route = allocation.getValue();
            vehicle.allocate(request);
            vehicle.updateRoute(state, route);
            decisionProcess.removeWaiting(request);
        }
        return request;
    }
}
