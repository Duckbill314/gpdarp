package gpdarp.decisionprocess;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;
import gpdarp.representation.route.Route;
import org.apache.commons.lang3.tuple.Pair;

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
    public Pair<Request, Route> next(Vehicle vehicle, DecisionProcessState state, List<Request> requests) {
        List<Pair<Request, Route>> requestPool = poolFilter.filterRequests(vehicle, state, requests);

        requestPool.forEach(pair -> {
            Request request = pair.getKey();
            Route route = pair.getValue();
            request.setPriority(priority(request, vehicle, route, state));
        });

        return requestPool.stream()
                .filter(e -> e.getKey().getPriority() <= 0)
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
     * Calculate the priority of a candidate request for a vehicle given a state.
     *
     * @param candidate the candidate request.
     * @param vehicle   the vehicle to be allocated to.
     * @param route     the route associated with the candidate.
     * @param state     the decision process state.
     * @return the priority of the candidate request.
     */
    public abstract double priority(Request candidate, Vehicle vehicle, Route route, DecisionProcessState state);
}
