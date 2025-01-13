package gpdarp.decisionprocess;

import gpdarp.core.*;
import gpdarp.representation.route.Route;
import org.apache.commons.lang3.tuple.Pair;
import util.Timer;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract decision process event.
 * It has a time that the event occurs, with natural comparison preferring the earlier event.
 *
 * @author gphhucarp, William Huang
 */
public abstract class DecisionProcessEvent implements Comparable<DecisionProcessEvent> {
    protected String name;
    protected int time;

    public DecisionProcessEvent(String name, int time) {
        this.name = name;
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

    @Override
    public String toString() { return String.format("%s at time %d", name, time); }

    /**
     * Helper method for handling vehicle allocation.
     *
     * @param decisionProcess the decision process that invoked this event.
     * @param request the request to be allocated.
     * @return the vehicle to which the request is allocated.
     */
    public Vehicle vehicleAllocation(DecisionProcess decisionProcess, Request request) {
        DecisionProcessState state = decisionProcess.getState();
        long startTime = Timer.getCpuTime();
        Pair<Vehicle, Route> allocation = decisionProcess.getVehiclePolicy().next(state, request);
        long endTime = Timer.getCpuTime();
        decisionProcess.addDecisionTime((double) (endTime - startTime) / 1000000000);

        if (allocation == null) {
            decisionProcess.addWaiting(request);
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
     * @param includeCharge   whether to also consider returning to a charging station.
     * @return the request allocation.
     */
    public Request requestAllocation(DecisionProcess decisionProcess, Vehicle vehicle, boolean includeCharge) {
        DecisionProcessState state = decisionProcess.getState();
        Node currPos = vehicle.getCurrPos();
        List<Request> waitingList = new ArrayList<>(decisionProcess.getWaitingList());

        if (includeCharge) {
            Instance instance = state.getInstance();
            Node station = instance.findClosestStation(currPos).clone();
            waitingList.add(new Request(currPos.getDepartureTime(), currPos, station));
        }

        long startTime = Timer.getCpuTime();
        Pair<Request, Route> allocation = decisionProcess.getRequestPolicy().next(vehicle, state, waitingList);
        long endTime = Timer.getCpuTime();
        decisionProcess.addDecisionTime((double) (endTime - startTime) / 1000000000);

        if (allocation == null) {
            return null;
        }

        Request request = allocation.getKey();
        Route route = allocation.getValue();

        if (request.getType() == Request.RequestType.REQUEST) {
            vehicle.allocate(request);
            decisionProcess.removeWaiting(request);
        }

        vehicle.updateRoute(state, route);
        return request;
    }

    /**
     * Iteratively adds waiting requests to construct a route,
     * until there are no more feasible waiting requests.
     *
     * @param decisionProcess the decision process that invoked this event.
     * @param vehicle         the vehicle to which a request is to be allocated.
     */
    public void constructiveHeuristic(DecisionProcess decisionProcess, Vehicle vehicle) {
        Request allocation;
        do {
            allocation = requestAllocation(decisionProcess, vehicle, false);
        } while (allocation != null);
    }
}
