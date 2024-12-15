package gpdarp.decisionprocess.allocationpolicy.ensemble.combiner;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.allocationpolicy.ensemble.EnsemblePolicy;
import gpdarp.decisionprocess.allocationpolicy.ensemble.Combiner;
import gpdarp.representation.VehiclePool;
import gpdarp.representation.route.Route;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * The aggregator combiner simply sums up the weighted priority calculated by all the elements,
 * and set the final priority as the weighted sum.
 *
 * @author gphhucarp, William Huang
 */
public class Aggregator extends Combiner {
    @Override
    public Map.Entry<Vehicle, Route> next(DecisionProcessState state, Request request, EnsemblePolicy ensemblePolicy) {
        VehiclePool vehiclePool = state.getPool();
        Set<Map.Entry<Vehicle, Route>> poolSet = vehiclePool.entrySet();

        poolSet.forEach(e -> e.getKey().setPriority(priority(e, state, request, ensemblePolicy)));

        return vehiclePool.entrySet().stream()
                .min((e1, e2) -> {
                    Vehicle v1 = e1.getKey();
                    Vehicle v2 = e2.getKey();
                    if (Double.compare(v1.getPriority(), v2.getPriority()) == 0) {
                        return ensemblePolicy.getTieBreaker().breakTie(v1, v2);
                    }
                    return Double.compare(v1.getPriority(), v2.getPriority());
                })
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Calculate the priority of a candidate vehicle (and its route) by an ensemble policy.
     *
     * @param candidate      the candidate vehicle + route.
     * @param state          the decision process state.
     * @param request        the request to be allocated.
     * @param ensemblePolicy the ensemble policy.
     *
     * @return the priority of the candidate vehicle calculated by the ensemble policy.
     */
    private double priority(Map.Entry<Vehicle, Route> candidate, DecisionProcessState state, Request request,
                            EnsemblePolicy ensemblePolicy) {
        double priority = 0;
        for (int i = 0; i < ensemblePolicy.size(); i++) {
            priority += ensemblePolicy.getPolicy(i).priority(candidate, state, request) *
                    ensemblePolicy.getWeight(i);
        }
        return priority;
    }
}
