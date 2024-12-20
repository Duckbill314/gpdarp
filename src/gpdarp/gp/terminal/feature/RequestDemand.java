package gpdarp.gp.terminal.feature;

import gpdarp.core.Request;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns the demand of the request.
 *
 * @author William Huang
 */
public class RequestDemand extends FeatureGPNode {
    public RequestDemand() {
        super();
        name = "DMND";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Request request = calcPriorityProblem.getRequest();

        return request.getDemand();
    }
}