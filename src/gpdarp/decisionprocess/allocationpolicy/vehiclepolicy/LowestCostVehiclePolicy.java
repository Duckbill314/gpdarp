package gpdarp.decisionprocess.allocationpolicy.vehiclepolicy;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.representation.route.Route;

import java.util.Map;

/**
 * The lowest cost policy calculates the planned route and assigns the vehicle a priority value based on the
 * planned route's cost.
 *
 * @author gphhucarp, William Huang
 */
public class LowestCostVehiclePolicy extends VehiclePolicy {
    public LowestCostVehiclePolicy() {
        super();
        name = "\"LowestCostVehiclePolicy\"";
    }

    @Override
    public double priority(Map.Entry<Vehicle, Route> candidate, DecisionProcessState state, Request request) {
        return candidate.getValue().getLength();
    }
}
