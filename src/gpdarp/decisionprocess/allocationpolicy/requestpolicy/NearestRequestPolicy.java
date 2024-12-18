package gpdarp.decisionprocess.allocationpolicy.requestpolicy;

import gpdarp.core.Node;
import gpdarp.core.Vehicle;
import gpdarp.core.WaitingRequest;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.representation.route.Route;

import java.util.Map;

/**
 * The nearest request policy selects the request whose pickup point is closest to the vehicle's current position
 * (if it is stationary) or current destination (if it is moving).
 * The priority is set to the length between the two aforementioned points.
 *
 * @author gphhucarp, William Huang
 */
public class NearestRequestPolicy extends RequestPolicy {
    public NearestRequestPolicy() {
        super();
        name = "\"NearestRequestPolicy\"";
    }
    @Override
    public double priority(Map.Entry<WaitingRequest, Route> candidate, DecisionProcessState state, Vehicle vehicle) {
        Node pos;
        if (!vehicle.isMoving()) {
            pos = vehicle.getCurrPos();
        }
        else {
            pos = vehicle.getCurrArc().to();
        }
        return pos.calcDist(candidate.getKey().getPickup());
    }
}
