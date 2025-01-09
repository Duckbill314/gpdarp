package gpdarp.gp.terminal.feature;

import gpdarp.core.*;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.representation.route.EphemeralRoute;
import gpdarp.representation.route.Route;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;

/**
 * Returns the best time taken for a vehicle other than the specified vehicle to pick up the request.
 *
 * @author William Huang
 */
public class OtherBestVehicle extends FeatureGPNode {
    private static final double LIMIT = Double.POSITIVE_INFINITY;

    public OtherBestVehicle() {
        super();
        name = "OBV";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        Request request = calcPriorityProblem.getRequest();
        DecisionProcessState state = calcPriorityProblem.getState();
        FeasiblePoolFilter poolFilter = new FeasiblePoolFilter();

        List<Pair<Vehicle, Route>> pool = poolFilter.filterVehicles(state, request);
        pool.removeIf(e -> e.getKey() == vehicle);

        return pool.stream()
                .map(pair -> pair.getValue().getEphemeralRoute())
                .map(route -> route.getRequestClones().stream()
                        .filter(req -> req.getId() == request.getId())
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .map(Request::getPickup)
                .map(Node::getArrivalTime)
                .min(Double::compare)
                .orElse((int) LIMIT);
    }
}
