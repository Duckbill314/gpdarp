package gpdarp.gp.terminal.feature;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.representation.route.EphemeralRoute;

import java.util.List;

/**
 * Returns the remaining capacity of the vehicle.
 *
 * @author William Huang
 */
public class VehicleCapacity extends FeatureGPNode {
    public VehicleCapacity() {
        super();
        name = "RQ";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        EphemeralRoute route = calcPriorityProblem.getRoute();

        List<Request> requests = route.getRequestClones();
        int demand = requests.stream()
                .map(Request::getDemand)
                .reduce(0, Integer::sum);

        return vehicle.getCapacity() - demand;
    }
}
