package gpdarp.gp.terminal.feature;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns the distance between the vehicle's current position and the request's dropoff point.
 * For charging requests, instead returns the distance between the vehicle's current position and the
 * specified charging station.
 *
 * @author William Huang
 */
public class DistanceToDropoff extends FeatureGPNode {
    public DistanceToDropoff() {
        super();
        name = "DTD";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        Request request = calcPriorityProblem.getRequest();

        return vehicle.getCurrPos().calcDist(request.getDropoff());
    }
}
