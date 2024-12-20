package gpdarp.gp.terminal.feature;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns the distance between the vehicle's current position and the request's pickup point.
 * For charging requests, instead returns the distance between the vehicle's current position and the
 * specified charging station.
 *
 * @author William Huang
 */
public class DistanceToPickup extends FeatureGPNode {
    public DistanceToPickup() {
        super();
        name = "DTP";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        Request request = calcPriorityProblem.getRequest();

        double value = 0;

        switch (request.getType()) {
            case REQUEST -> value = vehicle.getCurrPos().calcDist(request.getPickup());

            case CHARGE -> value = vehicle.getCurrPos().calcDist(request.getDropoff());
        }

        return value;
    }
}
