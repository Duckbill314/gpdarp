package gpdarp.decisionprocess;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;
import gpdarp.representation.RequestPool;
import gpdarp.representation.VehiclePool;
import gpdarp.representation.route.Route;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An allocation policy makes a decision on which vehicle should serve a request.
 *
 * @author gphhucarp, William Huang
 */
public abstract class AllocationPolicy {
    protected String name;
    protected PoolFilter poolFilter;
    protected TieBreaker tieBreaker;

    public AllocationPolicy(String name, PoolFilter poolFilter, TieBreaker tieBreaker) {
        this.name = name;
        this.poolFilter = poolFilter;
        this.tieBreaker = tieBreaker;
    }

    // Default constructor (with no name)
    public AllocationPolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        this(null, poolFilter, tieBreaker);
    }

    // Constructor with only pool filter specified
    public AllocationPolicy(PoolFilter poolFilter) {
        this(poolFilter, new SimpleTieBreaker());
    }

    // Constructor with only tiebreaker specified
    public AllocationPolicy(TieBreaker tieBreaker) {
        this(new FeasiblePoolFilter(), tieBreaker);
    }

    // Getters
    public String getName() {
        return name;
    }
    public PoolFilter getPoolFilter() {
        return poolFilter;
    }
    public TieBreaker getTieBreaker() {
        return tieBreaker;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Given the current decision process state and a request to be served,
     * select the vehicle to allocate the request to from the pool of eligible vehicles.
     *
     * @param state   the decision process state.
     * @param request the request to be served.
     *
     * @return the allocated vehicle and corresponding optimal route.
     */
    public Map.Entry<Vehicle, Route> next(DecisionProcessState state, Request request) {
        VehiclePool vehiclePool = poolFilter.filterVehicles(state, request);
        Set<Map.Entry<Vehicle, Route>> poolSet = vehiclePool.entrySet();

        poolSet.forEach(e -> e.getKey().setPriority(priority(e, state, request)));

        return poolSet.stream()
                .min((e1, e2) -> {
                    Vehicle v1 = e1.getKey();
                    Vehicle v2 = e2.getKey();
                    if (Double.compare(v1.getPriority(), v2.getPriority()) == 0) {
                        return tieBreaker.breakTie(v1, v2);
                    }
                    return Double.compare(v1.getPriority(), v2.getPriority());
                })
                .orElse(null);
    }

    /**
     * A variant of the main selection method.
     * Instead, only a singular vehicle is considered, and the pool comprises the different requests from the
     * waiting list that the vehicle can choose to accept.
     *
     * @param vehicle the vehicle that is trying to accept a request.
     * @param state the decision process state.
     * @param requests the waiting list of requests.
     *
     * @return the allocated request and corresponding optimal route.
     */
    public Map.Entry<Request, Route> next(Vehicle vehicle, DecisionProcessState state, List<Request> requests) {
        RequestPool requestPool = poolFilter.filterRequests(vehicle, state, requests);
        Set<Map.Entry<Request, Route>> poolSet = requestPool.entrySet();

        poolSet.forEach(e -> e.getKey().setPriority(priority(
                new AbstractMap.SimpleEntry<Vehicle, Route>(vehicle, e.getValue()), state, e.getKey())));

        return poolSet.stream()
                .min((e1, e2) -> {
                    Request r1 = e1.getKey();
                    Request r2 = e2.getKey();
                    if (Double.compare(r1.getPriority(), r2.getPriority()) == 0) {
                        return tieBreaker.breakTie(r1, r2);
                    }
                    return Double.compare(r1.getPriority(), r2.getPriority());
                })
                .orElse(null);
    }

    /**
     * Calculate the priority of a candidate vehicle (and its route) for a request given a state.
     *
     * @param candidate the candidate vehicle + route.
     * @param state     the decision process state.
     * @param request   the request to be allocated.
     *
     * @return the priority of the candidate vehicle.
     */
    public abstract double priority(Map.Entry<Vehicle, Route> candidate, DecisionProcessState state, Request request);
}
