package gpdarp.gp.terminal.feature;

import gpdarp.core.Arc;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.representation.route.EphemeralRoute;

import java.util.Objects;

/**
 * Returns the travel time between a request's pickup point (or for a charging event, station location)
 * and the node immediately preceding it in the planned route.
 *
 * @author William Huang
 */
public class TimeToPickup extends FeatureGPNode {
    public TimeToPickup() {
        super();
        name = "TVPU";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Request request = calcPriorityProblem.getRequest();
        EphemeralRoute route = calcPriorityProblem.getRoute();
        DecisionProcessState state = calcPriorityProblem.getState();

        int distance = 0;

        switch (request.getType()) {
            case REQUEST -> {
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

                distance = arc.length();
            }
            case CHARGE -> {
                distance = request.getPickup().calcDist(request.getDropoff());
            }
        }

        return state.getInstance().calculateTravelTime(distance);
    }
}
