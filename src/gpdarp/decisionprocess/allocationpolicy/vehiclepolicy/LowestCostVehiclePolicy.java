package gpdarp.decisionprocess.allocationpolicy.vehiclepolicy;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.representation.route.EphemeralRoute;

/**
 * The nearest vehicle policy selects the vehicle with the route that minimises the distance between the request
 * pickup point and the point immediately preceding it in the route.
 * The priority is set to the length between the two aforementioned points.
 *
 * @author gphhucarp, William Huang
 */
public class LowestCostVehiclePolicy extends VehiclePolicy {
    // A threshold is required to allow for no option to be selected.
    private static final double MAX_RANGE = 1000000;

    public LowestCostVehiclePolicy() {
        super();
        name = "\"LowestCostVehiclePolicy\"";
    }

    @Override
    public double priority(Vehicle candidate, Request request, EphemeralRoute route, DecisionProcessState state) {
        double priority = route.getTime() + state.getInstance().getLatenessPenalty() * route.calculatePenalty();
        return priority - MAX_RANGE;
    }
}
