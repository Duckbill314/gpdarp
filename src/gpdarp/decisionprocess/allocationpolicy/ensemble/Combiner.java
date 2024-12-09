package gpdarp.decisionprocess.allocationpolicy.ensemble;

import gpdarp.core.Arc;
import gpdarp.decisionprocess.DecisionProcessState;

import java.util.List;

/**
 * A combiner combines the decisions made by the policy elements in the ensemble,
 * and returns the final decision.
 */

public abstract class Combiner {

    public abstract Arc next(List<Arc> pool, NodeSeqRoute route, DecisionProcessState state, EnsemblePolicy ensemblePolicy);
}
