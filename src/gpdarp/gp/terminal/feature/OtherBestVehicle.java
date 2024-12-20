package gpdarp.gp.terminal.feature;

import gpdarp.core.Instance;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

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

        List<Vehicle> pool = poolFilter.filterVehicles(state, request);
        pool.remove(vehicle);

        return pool.stream()
                .map(v -> instance.calculateTravelTime(v.getCurrPos().calcDist(request.getPickup())))
                .min(Double::compare)
                .orElse((int) LIMIT);
    }
}
