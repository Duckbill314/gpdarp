package gpdarp.gp.terminal.feature;

import gpdarp.core.Request;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns whether the request is a charging action.
 * Provides a distinct incentive for charging actions.
 *
 * @author William Huang
 */
public class ChargingRequest extends FeatureGPNode {
    public ChargingRequest() {
        super();
        name = "CHRQ";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Request request = calcPriorityProblem.getRequest();
        if (request.getType() == Request.RequestType.CHARGE) {
            return 1000;
        }
        return 1;
    }
}