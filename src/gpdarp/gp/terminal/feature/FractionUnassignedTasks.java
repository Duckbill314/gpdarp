package gpdarp.gp.terminal.feature;

import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Feature: the fraction of tasks unassigned (unserved and not assigned to vehicles).
 *
 * Created by gphhucarp on 31/08/17.
 */
public class FractionUnassignedTasks extends FeatureGPNode {

    public FractionUnassignedTasks() {
        super();
        name = "FUT";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        DecisionProcessState state = calcPriorityProblem.getState();
        return 1.0 * state.getUnassignedTasks().size() /
                state.getInstance().getTasks().size();
    }
}
