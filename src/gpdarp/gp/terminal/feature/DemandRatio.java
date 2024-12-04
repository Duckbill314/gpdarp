package gpdarp.gp.terminal.feature;

import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * The demand ratio of a task: demand / capacity of the route.
 */

public class DemandRatio extends FeatureGPNode {
    public DemandRatio() {
        super();
        name = "DR";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        return calcPriorityProblem.getCandidate().getExpectedDemand() /
                calcPriorityProblem.getRoute().getCapacity();
    }
}
