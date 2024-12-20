package gpdarp.gp.terminal.feature;

import gpdarp.core.Vehicle;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns the capacity of the vehicle.
 *
 * @author William Huang
 */
public class VehicleCapacity extends FeatureGPNode {
    public VehicleCapacity() {
        super();
        name = "CAP";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();

        return vehicle.getCapacity();
    }
}
