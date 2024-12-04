package gpdarp.gp.terminal.feature;

import gpdarp.core.Arc;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

import java.util.List;

/**
 * The expected demand of the closest task to the candidate.
 * If there is no remaining task, return 0.
 */

public class Demand1 extends FeatureGPNode {
    public Demand1() {
        super();
        name = "DEM1";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Arc candidate = calcPriorityProblem.getCandidate();

        List<Arc> taskAdjacentList = calcPriorityProblem.getState()
                .getTaskAdjacencyList(candidate);

        if (taskAdjacentList.isEmpty())
            return 0;

        Arc task1 = taskAdjacentList.get(0);

        return task1.getExpectedDemand();
    }
}
