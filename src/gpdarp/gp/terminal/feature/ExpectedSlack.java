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
        int tCurr = state.getTime();
        int bestTime = (int) LIMIT;

        for (Pair<Vehicle, Route> candidate : pool) {
            EphemeralRoute candidateRoute = candidate.getValue().getEphemeralRoute();

            Request requestClone = candidateRoute.getRequestClones().stream()
                    .filter(r -> r.getId() == request.getId())
                    .findFirst()
                    .orElse(null);
            assert requestClone != null;

            Node pickup = requestClone.getPickup();
            Arc arc = Objects.requireNonNull(candidateRoute.getArcs().stream()
                    .filter(a -> a.to() == pickup)
                    .findFirst()
                    .orElse(null));
            int time = arc.to().getArrivalTime() - arc.from().getDepartureTime();
            bestTime = Math.min(bestTime, time);
        }

        return tMax - tCurr - bestTime;
    }
}
