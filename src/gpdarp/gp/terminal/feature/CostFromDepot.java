package gpdarp.gp.terminal.feature;

import gpdarp.core.Arc;
import gpdarp.core.Instance;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Feature: the cost from the depot to the head node of the task.
 *
 * Created by gphhucarp on 31/08/17.
 */
public class CostFromDepot extends FeatureGPNode {

    public CostFromDepot() {
        super();
        name = "CFD";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Instance instance = calcPriorityProblem.getState().getInstance();
        NodeSeqRoute route = calcPriorityProblem.getRoute();
        Arc candidate = calcPriorityProblem.getCandidate();
        return instance.getGraph().getEstDistance(instance.getDepot(), candidate.getFrom());
    }
}
