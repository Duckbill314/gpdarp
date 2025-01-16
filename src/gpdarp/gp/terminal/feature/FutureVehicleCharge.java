package gpdarp.gp.terminal.feature;

import gpdarp.core.Node;
import gpdarp.core.Vehicle;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.representation.route.EphemeralRoute;

/**
 * Returns the remaining battery charge of the vehicle at the latest point in its route.
 * If the latest point is a charging station, it implies that the vehicle will be restored to full charge.
 *
 * @author William Huang
 */
public class FutureVehicleCharge extends FeatureGPNode {
    public FutureVehicleCharge() {
        super();
        name = "FRT";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        EphemeralRoute route = calcPriorityProblem.getRoute();

        if (route.getEndpoint().getType() == Node.NodeType.STATION) {
            return vehicle.getChargeMax();
        }

        int distance = route.getLength();
        return vehicle.estimateDepletion(distance);
    }
}