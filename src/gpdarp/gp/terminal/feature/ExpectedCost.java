package gpdarp.gp.terminal.feature;

import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.representation.route.EphemeralRoute;

/**
 * Returns the expected cost (increase in objective value) of the candidate route.
 *
 * @author William Huang
 */
public class ExpectedCost extends FeatureGPNode {
    public ExpectedCost() {
        super();
        name = "COST";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        EphemeralRoute route = calcPriorityProblem.getRoute();
        DecisionProcessState state = calcPriorityProblem.getState();

        return route.getTime() + state.getInstance().getLatenessPenalty() * route.calculatePenalty();
    }
}
