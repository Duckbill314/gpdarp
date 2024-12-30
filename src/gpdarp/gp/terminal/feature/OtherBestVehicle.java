package gpdarp.gp.terminal.feature;

import gpdarp.core.Instance;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.representation.route.Route;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Returns the best time taken for a vehicle other than the specified vehicle to pick up the request.
 *
 * @author William Huang
 */
public class OtherBestVehicle extends FeatureGPNode {
    private static final double LIMIT = 1000;

    public OtherBestVehicle() {
        super();
        name = "OTHR";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        Request request = calcPriorityProblem.getRequest();
        DecisionProcessState state = calcPriorityProblem.getState();
        Instance instance = state.getInstance();
        FeasiblePoolFilter poolFilter = new FeasiblePoolFilter();

        List<Pair<Vehicle, Route>> pool = poolFilter.filterVehicles(state, request);
        pool.removeIf(e -> e.getKey() == vehicle);

        return 0;

        /*return pool.stream()
                .map(v -> instance.calculateTravelTime(v.getCurrPos().calcDist(request.getPickup())))
                .min(Double::compare)
                .orElse((int) LIMIT);*/
    }
}
