package gpdarp.decisionprocess;

import gpdarp.core.WaitingRequest;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.RandomTieBreaker;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;
import gpdarp.representation.RequestPool;
import gpdarp.representation.route.Route;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
     *
     * @return the allocated request and corresponding optimal route.
     */
    public Map.Entry<WaitingRequest, Route> next(Vehicle vehicle, DecisionProcessState state,
                                                 List<WaitingRequest> requests) {

        RequestPool requestPool = poolFilter.filterRequests(vehicle, state, requests);
        Set<Map.Entry<WaitingRequest, Route>> poolSet = requestPool.entrySet();

        poolSet.forEach(e -> e.getKey().setPriority(priority(e, state, vehicle)));

        return poolSet.stream()
                .min((e1, e2) -> {
                    WaitingRequest r1 = e1.getKey();
                    WaitingRequest r2 = e2.getKey();
                    if (Double.compare(r1.getPriority(), r2.getPriority()) == 0) {
                        return tieBreaker.breakTie(r1, r2);
                    }
                    return Double.compare(r1.getPriority(), r2.getPriority());
                })
                .orElse(null);
    }

    /**
     * Calculate the priority of a candidate request (and its route) given a state.
     *
     * @param candidate the candidate request + route.
     * @param state     the decision process state.
     * @param vehicle   the vehicle to be allocated to.
     *
     * @return the priority of the candidate request.
     */
    public abstract double priority(
            Map.Entry<WaitingRequest, Route> candidate, DecisionProcessState state, Vehicle vehicle);
}
