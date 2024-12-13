package gpdarp.decisionprocess.allocationpolicy;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.representation.route.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * The lowest cost policy calculates the planned route and assigns the vehicle a priority value based on the
 * planned route's cost.
 *
 * @author gphhucarp, William Huang
 */
public class LowestCostPolicy extends AllocationPolicy {
    public LowestCostPolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"LC\"";
    }

    public LowestCostPolicy() {
        this(new FeasiblePoolFilter(), new SimpleTieBreaker());
    }

    @Override
    public double priority(Vehicle candidate, DecisionProcessState state, Request request) {
        List<Request> requests = new ArrayList<>(candidate.getRequests());
        requests.add(request);
        Route route = candidate.recalculate(state, requests);
        return route.getLength();
    }
}
