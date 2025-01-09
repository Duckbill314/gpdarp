package gpdarp.decisionprocess.allocationpolicy.requestpolicy;

import gpdarp.core.Arc;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.representation.route.Route;

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
    public double priority(Request candidate, Vehicle vehicle, Route route, DecisionProcessState state) {
        double priority = 0;

        switch (candidate.getType()) {
            case REQUEST -> {
                Node to = candidate.getPickup();
                Node from = Objects.requireNonNull(route.getArcs().stream()
                        .filter(a -> a.to() == to)
                        .findFirst()
                        .orElse(null)).from();

                priority = from.calcDist(to);
            }
            case CHARGE -> {
                priority = vehicle.getCurrPos().calcDist(candidate.getDropoff());
            }
        }

        return priority - MAX_RANGE/state.getInstance().getNumVehicles();
    }
}
