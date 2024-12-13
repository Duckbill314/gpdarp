package gpdarp.decisionprocess;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;
import gpdarp.representation.Pool;
import gpdarp.representation.route.Route;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

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
        Pool filteredPool = poolFilter.filter(state, request);

        filteredPool.forEach((v, k) -> v.setPriority(priority(v, state, request)));

        return filteredPool.entrySet().stream()
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
     * Calculate the priority of a candidate vehicle for a request given a state.
     *
     * @param candidate the candidate vehicle.
     * @param state     the state.
     * @param request   the given request.
     * @return the priority of the candidate task.
     */
    public abstract double priority(Vehicle candidate, DecisionProcessState state, Request request);
}
