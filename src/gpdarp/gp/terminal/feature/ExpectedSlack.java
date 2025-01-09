package gpdarp.gp.terminal.feature;

import gpdarp.core.*;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.representation.route.Route;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;

/**
 * Returns the expected slack of the request.
 * It is calculated as: window end time - current time - shortest possible arrival time out of all vehicles.
 * It is a measure of urgency of a request.
 *
 * @author William Huang
 */
public class ExpectedSlack extends FeatureGPNode {
    private static final double LIMIT = 1000;

    public ExpectedSlack() {
        super();
        name = "SLACK";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Request request = calcPriorityProblem.getRequest();
        DecisionProcessState state = calcPriorityProblem.getState();

        if (request.getType() == Request.RequestType.CHARGE) {
            return LIMIT;
        }

        FeasiblePoolFilter poolFilter = new FeasiblePoolFilter();
        List<Pair<Vehicle, Route>> pool = poolFilter.filterVehicles(state, request);

        int tMax = request.getTMax();

        int bestTime = pool.stream()
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

        return tMax - bestTime;
    }
}
