package gpdarp.gp.terminal.feature;

import gpdarp.core.Request;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.representation.route.EphemeralRoute;

/**
 * Returns the minimal slack of the requests in the route.
 *
 * @author William Huang
 */
public class VehicleSlack extends FeatureGPNode {
    private static final double LIMIT = 1000;

    public VehicleSlack() {
        super();
        name = "VSLACK";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        EphemeralRoute route = calcPriorityProblem.getRoute();

        return route.getRequestClones().stream()
                .filter(r -> r.getType() == Request.RequestType.REQUEST)
                .map(r -> r.getTLate() - r.getPickup().getArrivalTime())
                .min(Double::compare)
                .orElse((int) LIMIT);
    }
}
