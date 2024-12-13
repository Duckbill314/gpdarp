package gpdarp.decisionprocess;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.representation.Pool;
import gpdarp.representation.route.Route;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * A pool filter uses some criteria to filter out vehicles from the pool given a state.
 * It is done implicitly through the decision process state object.
 * This preprocessing helps to improve the effectiveness and efficiency of decision-making during vehicle allocation.
 *
 * @author gphhucarp, William Huang
 */
public abstract class PoolFilter {
    public abstract Pool filter(DecisionProcessState state, Request request);
}
