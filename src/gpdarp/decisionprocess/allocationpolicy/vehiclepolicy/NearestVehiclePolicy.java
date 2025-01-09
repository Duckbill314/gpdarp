package gpdarp.decisionprocess.allocationpolicy.vehiclepolicy;

import gpdarp.core.Arc;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.representation.route.Route;

import java.util.Objects;

/**
 * The nearest vehicle policy selects the vehicle with the route that minimises the distance between the request
 * pickup point and the point immediately preceding it in the route.
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
    public double priority(Vehicle candidate, Request request, Route route, DecisionProcessState state) {
        Node to = request.getPickup();
        Node from = Objects.requireNonNull(route.getArcs().stream()
                .filter(a -> a.to() == to)
                .findFirst()
                .orElse(null)).from();

        double priority = from.calcDist(to);
        return priority - MAX_RANGE/state.getInstance().getNumVehicles();
    }
}
