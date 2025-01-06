package gpdarp.decisionprocess.allocationpolicy.vehiclepolicy;

import gpdarp.core.Node;
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
    // A threshold is required to allow for no option to be selected.
    private static final double MAX_RANGE = Math.ceil(Math.sqrt(Math.pow(2000, 2) + Math.pow(2000, 2)));

    public NearestVehiclePolicy() {
        super();
        name = "\"NearestVehiclePolicy\"";
    }

    @Override
    public double priority(Vehicle candidate, DecisionProcessState state, Request request) {
        Node pos;
        if (!candidate.isMoving()) {
            pos = candidate.getCurrPos();
        }
        else {
            pos = candidate.getCurrArc().to();
        }
        double priority = pos.calcDist(request.getPickup());
        return priority - MAX_RANGE/state.getInstance().getNumVehicles();
    }
}
