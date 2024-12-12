package gpdarp.decisionprocess;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.poolfilter.IdentityPoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;

import java.util.List;

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
        this(new IdentityPoolFilter(), tieBreaker);
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
     * @param state the decision process state.
     * @param request the request to be served.
     *
     * @return the next task to be served by the route.
     */
    public Vehicle next(DecisionProcessState state, Request request) {
        List<Vehicle> filteredPool = poolFilter.filter(request, state);

        if (filteredPool.isEmpty())
            return null;

        Vehicle next = filteredPool.getFirst();
        next.setPriority(priority(next, request, state));

        for (int i = 1; i < filteredPool.size(); i++) {
            Vehicle tmp = filteredPool.get(i);
            tmp.setPriority(priority(tmp, request, state));

            if (Double.compare(tmp.getPriority(), next.getPriority()) < 0 ||
                    (Double.compare(tmp.getPriority(), next.getPriority()) == 0 &&
                            tieBreaker.breakTie(tmp, next) < 0))
                next = tmp;
        }

        return next;
    }

    /**
     * Calculate the priority of a candidate vehicle for a request given a state.
     *
     * @param candidate the candidate vehicle.
     * @param request the given request.
     * @param state the state.
     *
     * @return the priority of the candidate task.
     */
    public abstract double priority(Vehicle candidate, Request request, DecisionProcessState state);
}
