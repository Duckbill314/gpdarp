package gpdarp.decisionprocess;

import gpdarp.core.WaitingRequest;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;

import java.util.List;

/**
 * A request allocation policy makes a decision on which request a vehicle should serve.
 *
 * @author William Huang
 */
public abstract class RequestPolicy {
    protected String name;
    protected PoolFilter poolFilter;
    protected TieBreaker tieBreaker;

    public RequestPolicy(String name, PoolFilter poolFilter, TieBreaker tieBreaker) {
        this.name = name;
        this.poolFilter = poolFilter;
        this.tieBreaker = tieBreaker;
    }

    // Default constructor (with no name)
    public RequestPolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        this(null, poolFilter, tieBreaker);
    }

    // Constructor with only pool filter specified
    public RequestPolicy(PoolFilter poolFilter) {
        this(poolFilter, new SimpleTieBreaker());
    }

    // Constructor with only tiebreaker specified
    public RequestPolicy(TieBreaker tieBreaker) {
        this(new FeasiblePoolFilter(), tieBreaker);
    }

    // Constructor with neither pool filter nor tiebreaker specified
    public RequestPolicy() { this(new FeasiblePoolFilter(), new SimpleTieBreaker()); }

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
     * Given a vehicle to serve a request, the current decision process state, and a list of requests,
     * select the request to allocate to the vehicle from the pool of eligible requests.
     *
     * @param vehicle the vehicle to accept a request.
     * @param state the decision process state.
     * @param requests the waiting list of requests.
     * @return the request to allocate to the vehicle.
     */
    public WaitingRequest next(Vehicle vehicle, DecisionProcessState state, List<WaitingRequest> requests) {
        List<WaitingRequest> pool = poolFilter.filterRequests(vehicle, state, requests);

        pool.forEach(request -> request.setPriority(priority(request, state, vehicle)));

        return pool.stream()
                .filter(r -> r.getPriority() <= 0)
                .min((r1, r2) -> {
                    if (Double.compare(r1.getPriority(), r2.getPriority()) == 0) {
                        return tieBreaker.breakTie(r1, r2);
                    }
                    return Double.compare(r1.getPriority(), r2.getPriority());
                })
                .orElse(null);
    }

    /**
     * Calculate the priority of a candidate request for a vehicle given a state.
     *
     * @param candidate the candidate request.
     * @param state     the decision process state.
     * @param vehicle   the vehicle to be allocated to.
     * @return the priority of the candidate request.
     */
    public abstract double priority(WaitingRequest candidate, DecisionProcessState state, Vehicle vehicle);
}
