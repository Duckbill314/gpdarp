package gpdarp.decisionprocess;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;

import java.util.List;

/**
 * A pool filter uses some criteria to filter out vehicles from the pool given a state.
 * This preprocessing helps to improve the effectiveness and efficiency of decision-making during vehicle allocation.
 *
 * @author gphhucarp, William Huang
 */
public abstract class PoolFilter {
    public abstract List<Vehicle> filter(List<Vehicle> pool, Request request, DecisionProcessState state);
}
