package gpdarp.gp.terminal.feature;

import gpdarp.core.Vehicle;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns the remaining battery charge of the vehicle at its current point.
 *
 * @author William Huang
 */
public class CurrentVehicleCharge extends FeatureGPNode {
    public CurrentVehicleCharge() {
        super();
        name = "RT";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        return vehicle.getChargeState();
    }
}