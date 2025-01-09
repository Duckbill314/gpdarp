package gpdarp.gp.terminal.feature;

import gpdarp.core.Vehicle;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns the remaining battery charge of the vehicle.
 *
 * @author William Huang
 */
public class VehicleCharge extends FeatureGPNode {
    public VehicleCharge() {
        super();
        name = "RT";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        return vehicle.getChargeState();
    }
}