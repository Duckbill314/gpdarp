package gpdarp.decisionprocess.poolfilter;

import gpdarp.core.Arc;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * A actual feasible pool filter filters the candidate tasks from the pool by selecting
 * only the tasks that are expected to be feasible to be added, i.e.
 * the tasks whose actual demands do not exceed the remaining capacity.
 */

public class ActFeasiblePoolFilter extends PoolFilter {

    @Override
    public List<Arc> filter(List<Arc> pool,
                            NodeSeqRoute route,
                            DecisionProcessState state) {
        double remainingCapacity = route.getCapacity() - route.getDemand();

        List<Arc> filtered = new ArrayList<>();
        for (Arc candidate : pool) {
            // check if the task is expected to be feasible or not
            if (state.getInstance().getActDemand(candidate) > remainingCapacity)
                continue;

            filtered.add(candidate);
        }

        return filtered;
    }
}
