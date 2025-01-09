package gpdarp.decisionprocess.allocationpolicy.vehiclepolicy;

import gpdarp.core.Arc;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.representation.route.EphemeralRoute;

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
    public double priority(Vehicle candidate, Request request, EphemeralRoute route, DecisionProcessState state) {
        Request requestClone = route.getRequestClones().stream()
                .filter(r -> r.getId() == request.getId())
                .findFirst()
                .orElse(null);
        assert requestClone != null;

        Node pickup = requestClone.getPickup();
        Arc arc = Objects.requireNonNull(route.getArcs().stream()
                .filter(a -> a.to() == pickup)
                .findFirst()
                .orElse(null));

        double priority = arc.length();
        return priority - MAX_RANGE/state.getInstance().getNumVehicles();
    }
}
