package gpdarp.decisionprocess.allocationpolicy.requestpolicy;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.representation.route.EphemeralRoute;

/**
 * The lowest cost request policy selects the request whose inclusion in the route fragment causes the least
 * increase in objective value.
 * The priority is set to the cost  of the fragment.
 *
 * @author gphhucarp, William Huang
 */
public class LowestCostRequestPolicy extends RequestPolicy {
    // A threshold is required to allow for no option to be selected.
    private static final double MAX_RANGE = 1000000;

    public LowestCostRequestPolicy() {
        super();
        name = "\"LowestCostRequestPolicy\"";
    }

    @Override
    public double priority(Request candidate, Vehicle vehicle, EphemeralRoute route, DecisionProcessState state) {
        double priority = route.getTime() + state.getInstance().getLatenessPenalty() * route.calculatePenalty();
        return priority - MAX_RANGE;
    }
}
