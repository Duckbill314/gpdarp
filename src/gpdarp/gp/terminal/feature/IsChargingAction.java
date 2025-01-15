package gpdarp.gp.terminal.feature;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns a larger value if a request is a charging request.
 * Used to provide an incentive for charging requests.
 *
 * @author William Huang
 */
public class IsChargingAction extends FeatureGPNode {
    public IsChargingAction() {
        super();
        name = "CHRG";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        Request request = calcPriorityProblem.getRequest();
        if (request.getType() == Request.RequestType.CHARGE) {
            return 1000; //vehicle.getChargeFillRate();
        }
        return 1;
    }
}
