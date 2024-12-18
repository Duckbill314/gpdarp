package gpdarp.decisionprocess;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.RandomTieBreaker;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;
import gpdarp.representation.VehiclePool;
import gpdarp.representation.route.Route;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.Map;
import java.util.Set;

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
