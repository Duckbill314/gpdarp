package gpdarp.decisionprocess.allocationpolicy;

import gpdarp.core.Arc;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.poolfilter.IdentityPoolFilter;

/**
 * The feasibility proreactive policy checks the expected demand of the candidate.
 * If the expected demand does not exceed the remaining capacity, then continue the ervice.
 * Otherwise, refill (priority = capacity - routeDemand - taskDemand < 0).
 */

public class FeasibilityPolicy extends AllocationPolicy {
    public FeasibilityPolicy(PoolFilter poolFilter) {
        super(poolFilter);
        name = "\"FSB\"";
    }

    public FeasibilityPolicy() {
        this(new IdentityPoolFilter());
    }

    @Override
    public double priority(Vehicle candidate, NodeSeqRoute route, DecisionProcessState state) {
        return route.getCapacity() - route.getDemand() - candidate.getExpectedDemand();
    }
}
