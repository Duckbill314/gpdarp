package gpdarp.gp.terminal.feature;

import gpdarp.core.Arc;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

public class FloodFill extends FeatureGPNode {

    public FloodFill() {
        super();
        name = "FF";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Arc candidate = calcPriorityProblem.getCandidate();
        DecisionProcessState state = calcPriorityProblem.getState();
        return state.isOnFloods(candidate).size() + state.isOnFloods(candidate.getInverse()).size();
    }
}
