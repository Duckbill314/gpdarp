package gpdarp.gp.terminal.feature;

import gpdarp.core.Vehicle;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns the current battery charge state of the vehicle.
 *
 * @author William Huang
 */
public class VehicleCharge extends FeatureGPNode {
    public VehicleCharge() {
        super();
        name = "CHRG";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();

        return vehicle.getChargeState();
    }
}