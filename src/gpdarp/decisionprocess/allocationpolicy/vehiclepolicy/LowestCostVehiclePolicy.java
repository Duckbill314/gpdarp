package gpdarp.decisionprocess.allocationpolicy.vehiclepolicy;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.representation.route.Route;

import java.util.Map;

/**
 * The lowest cost policy calculates the planned route and assigns the vehicle a priority value based on the
 * planned route's cost.
 *
 * @author gphhucarp, William Huang
 */
public class LowestCostVehiclePolicy extends VehiclePolicy {
    public LowestCostVehiclePolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"LC\"";
    }

    public LowestCostVehiclePolicy() {
        this(new FeasiblePoolFilter(), new SimpleTieBreaker());
    }

    @Override
    public double priority(Map.Entry<Vehicle, Route> candidate, DecisionProcessState state, Request request) {
        return candidate.getValue().getLength();
    }
}
