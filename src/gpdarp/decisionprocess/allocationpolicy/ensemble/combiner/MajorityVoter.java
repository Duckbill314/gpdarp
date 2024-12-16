package gpdarp.decisionprocess.allocationpolicy.ensemble.combiner;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.allocationpolicy.ensemble.Combiner;
import gpdarp.decisionprocess.allocationpolicy.ensemble.EnsemblePolicy;
import gpdarp.representation.route.Route;

import java.util.*;

/**
 * The majority voter selects the next candidate by majority voting.
 * Each element in the ensemble votes for the candidate with the best priority of it.
 * Then the element with the most votes will be selected.
 *
 * @author gphhucarp, William Huang
 */
public class MajorityVoter extends Combiner {
    @Override
    public Map.Entry<Vehicle, Route> next(DecisionProcessState state, Request request, EnsemblePolicy ensemblePolicy) {
        List<Map.Entry<Vehicle, Route>> pool = new ArrayList<>(state.getPool().entrySet());
        ArrayList<Double> votes = new ArrayList<>();
        for (int i = 0; i < pool.size(); i++) {
            votes.add(0.0);
        }

        for (int ele = 0; ele < ensemblePolicy.size(); ele++) {
            VehiclePolicy policy = ensemblePolicy.getPolicy(ele);

            pool.forEach(e -> e.getKey().setPriority(policy.priority(e, state, request)));

            Map.Entry<Vehicle, Route> best = pool.stream()
                    .min((e1, e2) -> {
                        Vehicle v1 = e1.getKey();
                        Vehicle v2 = e2.getKey();
                        if (Double.compare(v1.getPriority(), v2.getPriority()) == 0) {
                            return ensemblePolicy.getTieBreaker().breakTie(v1, v2);
                        }
                        return Double.compare(v1.getPriority(), v2.getPriority());
                    })
                    .orElseThrow(NoSuchElementException::new);

            int bestIdx = pool.indexOf(best);
            votes.set(bestIdx, votes.get(bestIdx) + ensemblePolicy.getWeight(ele));
        }

        Double maxVotes = votes.stream()
                .max(Double::compare)
                .orElseThrow(NoSuchElementException::new);

        return pool.get(votes.indexOf(maxVotes));
    }
}
