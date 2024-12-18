package gpdarp.decisionprocess;

import gpdarp.core.WaitingRequest;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;

import java.util.List;

/**
 * A pool filter uses some criteria to filter out vehicles or requests from the pool, given a state.
 * It is done implicitly through the decision process state object.
 * This preprocessing helps to improve the effectiveness and efficiency of decision-making during vehicle allocation.
 *
 * @author gphhucarp, William Huang
 */
public abstract class PoolFilter {
    /**
     * Given a state and a request to be fulfilled, filter for the vehicles that can feasibly serve the request.
     *
     * @param state the decision process state.
     * @param request the request to be fulfilled.
     *
     * @return the filtered pool.
     */
    public abstract List<Vehicle> filterVehicles(DecisionProcessState state, Request request);

    /**
     * Given a state, a vehicle, and a list of requests, filter for the requests that the vehicle can feasibly serve.
     *
     * @param vehicle the vehicle that is attempting to accept a request from the waiting list.
     * @param state the decision process state.
     * @param requests the waiting list of requests.
     *
     * @return the filtered pool.
     */
    public abstract List<WaitingRequest> filterRequests(Vehicle vehicle, DecisionProcessState state,
                                                 List<WaitingRequest> requests);
}
