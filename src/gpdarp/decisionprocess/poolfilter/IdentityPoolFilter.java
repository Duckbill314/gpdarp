package gpdarp.decisionprocess.poolfilter;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;

import java.util.List;

/**
 * The identity pool filter does nothing, but simply returns the pool.
 * It is called "identity" since the filtered pool is the same as the given pool.
 */

public class IdentityPoolFilter extends PoolFilter {
    @Override
    public List<Vehicle> filter(List<Vehicle> pool, Request request, DecisionProcessState state) { return pool; }
}
