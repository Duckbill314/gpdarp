package gpdarp.decisionprocess.allocationpolicy.ensemble.combiner;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.allocationpolicy.ensemble.Combiner;
import gpdarp.decisionprocess.allocationpolicy.ensemble.EnsemblePolicy;

import java.util.List;

/**
 * The majority voter selects the next candidate by majority voting.
 * Each element in the ensemble votes for the candidate with the best priority of it.
 * Then the element with the most votes will be selected.
 *
 * @author gphhucarp, William Huang
 */
public class MajorityVoter extends Combiner {
    @Override
    public Vehicle next(Request request, DecisionProcessState state, EnsemblePolicy ensemblePolicy) {
        List<Vehicle> pool = state.getInstance().getVehicles();
        int[] votes = new int[pool.size()];

        for (int ele = 0; ele < ensemblePolicy.size(); ele++) {
            AllocationPolicy policy = ensemblePolicy.getPolicy(ele);

            int bestIdx = 0;
            Vehicle best = pool.get(bestIdx);
            best.setPriority(policy.priority(best, request, state));

            for (int i = 1; i < pool.size(); i++) {
                Vehicle tmp = pool.get(i);
                tmp.setPriority(policy.priority(tmp, request, state));

                if (Double.compare(tmp.getPriority(), best.getPriority()) < 0 ||
                        (Double.compare(tmp.getPriority(), best.getPriority()) == 0 &&
                                policy.getTieBreaker().breakTie(tmp, best) < 0)) {
                    bestIdx = i;
                    best = tmp;
                }
            }
            votes[bestIdx] += (int) ensemblePolicy.getWeight(ele);
        }
        int maxVotes = 0;
        Vehicle next = null;

        for (int i = 0; i < pool.size(); i++) {
            if (maxVotes < votes[i]) {
                maxVotes = votes[i];
                next = pool.get(i);
            }
        }
        return next;
    }
}
