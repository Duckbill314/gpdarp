package gpdarp.gp.terminal.feature;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns the charging rate of the request. Used to provide an incentive for charging requests.
 *
 * @author William Huang
 */
public class ChargingRate extends FeatureGPNode {
    public ChargingRate() {
        super();
        name = "CHRG";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        Request request = calcPriorityProblem.getRequest();
        if (request.getType() == Request.RequestType.CHARGE) {
            return vehicle.getChargeFillRate();
        }
        return 0;
    }
}
