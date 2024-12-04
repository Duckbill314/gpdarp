package gpdarp.algorithm.pilotsearch;

import gpdarp.core.Arc;
import gpdarp.decisionprocess.RoutingPolicy;
import gpdarp.decisionprocess.reactive.ReactiveDecisionSituation;

public abstract class PilotSearcher {
    /**
     * Select the next task of the give route based on the decision situation and policy.
     * @param rds the decision situation.
     * @param routingPolicy the routing policy.
     * @return the selected next task.
     */
    public abstract Arc next(ReactiveDecisionSituation rds,
                             RoutingPolicy routingPolicy);
}
