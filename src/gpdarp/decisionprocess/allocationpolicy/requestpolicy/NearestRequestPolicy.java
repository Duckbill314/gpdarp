package gpdarp.decisionprocess.allocationpolicy.requestpolicy;

import gpdarp.core.Arc;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.representation.route.EphemeralRoute;

import java.util.Objects;

/**
 * The nearest request policy selects the request whose pickup point (or for a charging event, station location)
 * is closest to the point immediately preceding it in the route.
 * The priority is set to the length between the two aforementioned points.
 *
 * @author gphhucarp, William Huang
 */
public class NearestRequestPolicy extends RequestPolicy {
    // A threshold is required to allow for no option to be selected.
    private static final double MAX_RANGE = Math.ceil(Math.sqrt(Math.pow(2000, 2) + Math.pow(2000, 2)));

    public NearestRequestPolicy() {
        super();
        name = "\"NearestRequestPolicy\"";
    }

    @Override
    public double priority(Request candidate, Vehicle vehicle, EphemeralRoute route, DecisionProcessState state) {
        double priority = 0;

        switch (candidate.getType()) {
            case REQUEST -> {
                Request requestClone = route.getRequestClones().stream()
                        .filter(r -> r.getId() == candidate.getId())
                        .findFirst()
                        .orElse(null);
                assert requestClone != null;

                Node pickup = requestClone.getPickup();
                Arc arc = Objects.requireNonNull(route.getArcs().stream()
                        .filter(a -> a.to() == pickup)
                        .findFirst()
                        .orElse(null));

                priority = arc.length();
            }
            case CHARGE -> {
                priority = candidate.getPickup().calcDist(candidate.getDropoff());
            }
        }

        return priority - MAX_RANGE/state.getInstance().getNumVehicles();
    }
}
