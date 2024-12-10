package gpdarp.decisionprocess.allocationpolicy.ensemble;

import gpdarp.core.Arc;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;

import java.util.List;

/**
 * A combiner combines the decisions made by the policy elements in the ensemble,
 * and returns the final decision.
 */

public abstract class Combiner {

    public abstract Vehicle next(List<Vehicle> pool, Request request, DecisionProcessState state,
                                 EnsemblePolicy ensemblePolicy);
}
