package gpdarp.gp.terminal.feature;

import gpdarp.core.Arc;
import gpdarp.core.Instance;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Feature: the cost from the tail node of the task to the depot.
 *
 * Created by gphhucarp on 31/08/17.
 */
public class CostToDepot extends FeatureGPNode {

    public CostToDepot() {
        super();
        name = "CTD";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Instance instance = calcPriorityProblem.getState().getInstance();
        NodeSeqRoute route = calcPriorityProblem.getRoute();
        Arc candidate = calcPriorityProblem.getCandidate();
        return instance.getGraph().getEstDistance(candidate.getTo(), instance.getDepot());
    }
}
