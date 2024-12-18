package gpdarp.decisionprocess.allocationpolicy.vehiclepolicy;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.representation.route.Route;

import java.util.Map;

/**
 * The lowest penalty policy calculates the planned route and assigns the vehicle a priority value based on the
 * planned route's penalty.
 *
 * @author gphhucarp, William Huang
 */
public class LowestPenaltyVehiclePolicy extends VehiclePolicy {
    public LowestPenaltyVehiclePolicy() {
        super();
        name = "\"LowestPenaltyVehiclePolicy\"";
    }

    @Override
    public double priority(Map.Entry<Vehicle, Route> candidate, DecisionProcessState state, Request request) {
        return candidate.getValue().calculatePenalty();
    }
}
