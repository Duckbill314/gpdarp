package gpdarp.decisionprocess;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.representation.RequestPool;
import gpdarp.representation.VehiclePool;

import java.util.List;

/**
 * A pool filter uses some criteria to filter out vehicles from the pool given a state.
 * It is done implicitly through the decision process state object.
 * This preprocessing helps to improve the effectiveness and efficiency of decision-making during vehicle allocation.
 *
 * @author gphhucarp, William Huang
 */
public abstract class PoolFilter {
    /**
     * Given a state and a request to be fulfilled, filter for the optimal route of each vehicle.
     *
     * @param state the decision process state.
     * @param request the request to be fulfilled.
     *
     * @return the filtered pool, containing potentially multiple vehicles, but at most one route per vehicle.
     */
    public abstract VehiclePool filterVehicles(DecisionProcessState state, Request request);

    /**
     * Given a vehicle and a list of requests, filter for the feasible requests.
     *
     * @param vehicle the vehicle that is attempting to accept a request from the waiting list.
     * @param state the decision process state.
     * @param requests the waiting list of requests.
     *
     * @return the filtered pool, containing potentially multiple routes for the one vehicle.
     */
    public abstract RequestPool filterRequests(Vehicle vehicle, DecisionProcessState state, List<Request> requests);
}
