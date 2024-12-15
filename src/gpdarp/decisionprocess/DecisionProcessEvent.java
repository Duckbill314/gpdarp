package gpdarp.decisionprocess;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.representation.route.Route;

import java.util.List;
import java.util.Map;

/**
 * An abstract decision process event.
 * It has a time that the event occurs.
 * Natural comparison prefers the earlier event.
 *
 * @author gphhucarp
 */
public abstract class DecisionProcessEvent implements Comparable<DecisionProcessEvent> {
    protected int time;

    public DecisionProcessEvent(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public abstract void trigger(DecisionProcess decisionProcess);

    @Override
    public int compareTo(DecisionProcessEvent o) { return Integer.compare(time, o.time); }

    public Vehicle vehicleAllocation(DecisionProcess decisionProcess, Request request) {
        DecisionProcessState state = decisionProcess.getState();
        Map.Entry<Vehicle, Route> allocation = decisionProcess.getAllocationPolicy().next(state, request);

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

    public void requestAllocation(DecisionProcess decisionProcess, Vehicle vehicle) {
        DecisionProcessState state = decisionProcess.getState();
        List<Request> waitingList = decisionProcess.getWaitingList();
        Map.Entry<Request, Route> allocation = decisionProcess.getAllocationPolicy().next(vehicle, state, waitingList);

        if (allocation != null) {
            Request request = allocation.getKey();
            Route route = allocation.getValue();
            vehicle.allocate(request);
            vehicle.updateRoute(state, route);
            waitingList.remove(request);
        }
    }
}
