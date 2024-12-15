package gpdarp.decisionprocess.allocationpolicy;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;
import gpdarp.representation.route.Route;

import java.util.Map;

/**
 * The lowest penalty policy calculates the planned route and assigns the vehicle a priority value based on the
 * planned route's penalty.
 *
 * @author gphhucarp, William Huang
 */
public class LowestPenaltyPolicy extends AllocationPolicy {
    public LowestPenaltyPolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"LP\"";
    }

    public LowestPenaltyPolicy() {
        this(new FeasiblePoolFilter(), new SimpleTieBreaker());
    }

    @Override
    public double priority(Map.Entry<Vehicle, Route> candidate, DecisionProcessState state, Request request) {
        return candidate.getValue().calculatePenalty();
    }
}
