package gpdarp.decisionprocess.allocationpolicy.ensemble;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;

/**
 * A combiner combines the decisions made by the policy elements in the ensemble,
 * and returns the final decision.
 *
 * @author gphhucarp
 */

public abstract class Combiner {
    public abstract Vehicle next(Request request, DecisionProcessState state, EnsemblePolicy ensemblePolicy);
}
