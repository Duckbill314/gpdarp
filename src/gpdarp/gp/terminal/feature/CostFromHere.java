package gpdarp.gp.terminal.feature;

import gpdarp.core.Arc;
import gpdarp.core.Instance;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Feature: the cost from here (the current node) to the head node of the task.
 *
 * Created by gphhucarp on 30/08/17.
 */
public class CostFromHere extends FeatureGPNode {

    public CostFromHere() {
        super();
        name = "CFH";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Instance instance = calcPriorityProblem.getState().getInstance();
        NodeSeqRoute route = calcPriorityProblem.getRoute();
        Arc candidate = calcPriorityProblem.getCandidate();
        return instance.getGraph().getEstDistance(route.currPos(), candidate.getFrom());
    }
}
