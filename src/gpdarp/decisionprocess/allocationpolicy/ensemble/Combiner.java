package gpdarp.decisionprocess.allocationpolicy.ensemble;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.representation.route.Route;

import java.util.Map;

/**
 * A combiner combines the decisions made by the policy elements in the ensemble,
 * and returns the final decision.
 *
 * @author gphhucarp
 */

public abstract class Combiner {
    public abstract Map.Entry<Vehicle, Route> next(DecisionProcessState state, Request request,
                                                   EnsemblePolicy ensemblePolicy);
}
