package gpdarp.gp.terminal.feature;

import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Feature: the serving cost of the candidate task.
 *
 * Created by gphhucarp on 30/08/17.
 */
public class ServeCost extends FeatureGPNode {
    public ServeCost() {
        super();
        name = "SC";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        return calcPriorityProblem.getCandidate().getServeCost();
    }
}
