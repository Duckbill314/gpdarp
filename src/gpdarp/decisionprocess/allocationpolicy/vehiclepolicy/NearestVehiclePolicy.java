package gpdarp.decisionprocess.allocationpolicy.vehiclepolicy;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.DecisionProcessState;

/**
 * The nearest vehicle policy selects the vehicle whose current position (if it is stationary) or whose current
 * destination (if it is moving) is closest to the request pickup point.
 * The priority is set to the length between the two aforementioned points.
 *
 * @author gphhucarp, William Huang
 */
public class NearestVehiclePolicy extends VehiclePolicy {
    public NearestVehiclePolicy() {
        super();
        name = "\"NearestVehiclePolicy\"";
    }

    @Override
    public double priority(Vehicle candidate, DecisionProcessState state, Request request) {
        return candidate.getCurrPos().calcDist(request.getPickup());
    }
}
