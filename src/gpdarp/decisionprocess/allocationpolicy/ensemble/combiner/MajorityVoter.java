package gpdarp.decisionprocess.allocationpolicy.ensemble.combiner;

import gpdarp.core.Arc;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.allocationpolicy.ensemble.Combiner;
import gpdarp.decisionprocess.allocationpolicy.ensemble.EnsemblePolicy;

import java.util.List;

/**
 * The majority voter selects the next candidate by majority voting.
 * Each element in the ensemble votes for the candidate with the best priority of it.
 * Then the element with the most votes will be selected.
 */

public class MajorityVoter extends Combiner {

    @Override
    public Arc next(List<Arc> pool, NodeSeqRoute route, DecisionProcessState state, EnsemblePolicy ensemblePolicy) {
        int[] votes = new int[pool.size()];

        for (int ele = 0; ele < ensemblePolicy.size(); ele++) {
            AllocationPolicy policy = ensemblePolicy.getPolicy(ele);

            int bestIdx = 0;
            Arc best = pool.get(bestIdx);
            best.setPriority(policy.priority(best, route, state));

            for (int i = 1; i < pool.size(); i++) {
                Arc tmp = pool.get(i);
                tmp.setPriority(policy.priority(tmp, route, state));

                if (Double.compare(tmp.getPriority(), best.getPriority()) < 0 ||
                        (Double.compare(tmp.getPriority(), best.getPriority()) == 0 &&
                                policy.getTieBreaker().breakTie(tmp, best) < 0)) {
                    bestIdx = i;
                    best = tmp;
                }
            }

            votes[bestIdx] += ensemblePolicy.getWeight(ele);
        }

        int maxVotes = 0;
        Arc next = null;

        for (int i = 0; i < pool.size(); i++) {
            if (maxVotes < votes[i]) {
                maxVotes = votes[i];
                next = pool.get(i);
            }
        }

        return next;
    }
}
