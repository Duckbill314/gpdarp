package gpdarp.gp.terminal.feature;

import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Feature: the fullness of the route. 0 if totally empty, 1 totally full.
 */

public class Fullness extends FeatureGPNode {

    public Fullness() {
        super();
        name = "FULL";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        NodeSeqRoute route = calcPriorityProblem.getRoute();
        return route.getDemand() / route.getCapacity();
    }
}
