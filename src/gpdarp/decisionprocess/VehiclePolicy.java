package gpdarp.decisionprocess;

import gpdarp.core.Instance;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;
import gpdarp.representation.route.EphemeralRoute;
import gpdarp.representation.route.Route;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * A vehicle allocation policy makes a decision on which vehicle should serve a request.
 *
 * @author gphhucarp, William Huang
 */
public abstract class VehiclePolicy {
    protected String name;
    protected PoolFilter poolFilter;
    protected TieBreaker tieBreaker;

    public VehiclePolicy(String name, PoolFilter poolFilter, TieBreaker tieBreaker) {
        this.name = name;
        this.poolFilter = poolFilter;
        this.tieBreaker = tieBreaker;
    }

    // Default constructor (with no name)
    public VehiclePolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        this(null, poolFilter, tieBreaker);
    }

    // Constructor with only pool filter specified
    public VehiclePolicy(PoolFilter poolFilter) {
        this(poolFilter, new SimpleTieBreaker());
    }

    // Constructor with only tiebreaker specified
    public VehiclePolicy(TieBreaker tieBreaker) {
        this(new FeasiblePoolFilter(), tieBreaker);
    }

    // Constructor with neither pool filter nor tiebreaker specified
    public VehiclePolicy() { this(new FeasiblePoolFilter(), new SimpleTieBreaker()); }

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
     * @param lastCall if it is the last call, the threshold can be ignored.
     * @return the allocated vehicle and corresponding optimal route.
     */
    public Pair<Vehicle, Route> next(DecisionProcessState state, Request request, boolean lastCall) {
        List<Pair<Vehicle, Route>> vehiclePool = poolFilter.filterVehicles(state, request);

        vehiclePool.forEach(pair -> {
            Vehicle vehicle = pair.getKey();
            Route route = pair.getValue();
            route.setPriority(priority(vehicle, request, route.getEphemeralRoute(), state));
        });

        return vehiclePool.stream()
                .filter(e -> {
                    if (lastCall) {
                        return true;
                    }
                    return e.getValue().getPriority() <= 0;
                })
                .min((e1, e2) -> {
                    if (Double.compare(e1.getValue().getPriority(), e2.getValue().getPriority()) == 0) {
                        return tieBreaker.breakTie(e1.getKey(), e2.getKey());
                    }
                    return Double.compare(e1.getValue().getPriority(), e2.getValue().getPriority());
                })
                .orElse(null);
    }

    /**
     * Calculate the priority of a candidate vehicle for a request given a state.
     *
     * @param candidate the candidate vehicle.
     * @param request   the request to be allocated.
     * @param route     a clone of the route associated with the candidate.
     * @param state     the decision process state.
     * @return the priority of the candidate vehicle.
     */
    public abstract double priority(Vehicle candidate, Request request, EphemeralRoute route, DecisionProcessState state);

    /**
     * A variant of the allocation method used for debugging purposes.
     * It circumvents the feasibility pool filter to offer all possible routes.
     *
     * @param state the decision process state.
     * @param request the request to be allocated.
     * @return a list of all the generated routes.
     */
    public List<Pair<Vehicle, Route>> debugNext(DecisionProcessState state, Request request) {
        Instance instance = state.getInstance();
        List<Vehicle> vehicles = new ArrayList<>(instance.getVehicles());
        List<Pair<Vehicle, Route>> vehiclePool = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            List<Request> requests = new ArrayList<>(vehicle.getRequests());
            requests.add(request);
            List<Route> routes = ((FeasiblePoolFilter) poolFilter).debugRecalculate(vehicle, state, requests);

            for (Route route : routes) {
                vehiclePool.add(Pair.of(vehicle, route));
            }
        }

        vehiclePool.forEach(pair -> {
            Vehicle vehicle = pair.getKey();
            Route route = pair.getValue();
            route.setPriority(priority(vehicle, request, route.getEphemeralRoute(), state));
        });

        return vehiclePool;
    }
}
