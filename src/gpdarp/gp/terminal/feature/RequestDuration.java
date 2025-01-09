package gpdarp.gp.terminal.feature;

import gpdarp.core.Request;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.representation.route.EphemeralRoute;

/**
 * Returns the ride time of the request.
 *
 * @author William Huang
 */
public class RequestDuration extends FeatureGPNode {
    public RequestDuration() {
        super();
        name = "DUR";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Request request = calcPriorityProblem.getRequest();
        EphemeralRoute route = calcPriorityProblem.getRoute();

        Request requestClone = route.getRequestClones().stream()
                .filter(r -> r.getId() == request.getId())
                .findFirst().orElse(null);
        assert requestClone != null;

        return requestClone.calcRideTime();
    }
}
