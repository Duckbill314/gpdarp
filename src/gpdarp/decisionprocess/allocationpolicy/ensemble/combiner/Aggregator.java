package gpdarp.decisionprocess.allocationpolicy.ensemble.combiner;

import gpdarp.core.Arc;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.allocationpolicy.ensemble.EnsemblePolicy;
import gpdarp.decisionprocess.allocationpolicy.ensemble.Combiner;

import java.util.List;

/**
 * The aggregator combiner simply sums up the weighted priority calculated by all the elements,
 * and set the final priority as the weighted sum.
 *
 * @author gphhucarp, William Huang
 */
public class Aggregator extends Combiner {

    @Override
    public Vehicle next(List<Vehicle> pool, Request request, DecisionProcessState state, EnsemblePolicy ensemblePolicy) {
        Vehicle next = pool.getFirst();
        next.setPriority(priority(next, request, state, ensemblePolicy));

        for (int i = 1; i < pool.size(); i++) {
            Vehicle tmp = pool.get(i);
            tmp.setPriority(priority(tmp, request, state, ensemblePolicy));

            if (Double.compare(tmp.getPriority(), next.getPriority()) < 0 ||
                    (Double.compare(tmp.getPriority(), next.getPriority()) == 0 &&
                            ensemblePolicy.getTieBreaker().breakTie(tmp, next) < 0))
                next = tmp;
        }

        return next;
    }

    /**
     * Calculate the priority of a candidate vehicle by an ensemble policy.
     *
     * @param vehicle the vehicle whose priority is to be calculated.
     * @param request the request to be allocated.
     * @param state the decision process state.
     * @param ensemblePolicy the ensemble policy.
     *
     * @return the priority of the vehicle calculated by the ensemble policy.
     */
    private double priority(Vehicle vehicle, Request request, DecisionProcessState state,
                            EnsemblePolicy ensemblePolicy) {
        double priority = 0;
        for (int i = 0; i < ensemblePolicy.size(); i++) {
            priority += ensemblePolicy.getPolicy(i).priority(vehicle, request, state) *
                    ensemblePolicy.getWeight(i);
        }

        return priority;
    }
}
