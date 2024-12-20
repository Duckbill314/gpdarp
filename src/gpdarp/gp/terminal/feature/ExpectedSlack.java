package gpdarp.gp.terminal.feature;

import gpdarp.core.Instance;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns the expected slack for serving the request.
 * It is a measure of urgency of a request.
 *
 * @author William Huang
 */
public class ExpectedSlack extends FeatureGPNode {
    private static final double LIMIT = 1000;

    public ExpectedSlack() {
        super();
        name = "SLCK";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        Request request = calcPriorityProblem.getRequest();
        DecisionProcessState state = calcPriorityProblem.getState();
        Instance instance = state.getInstance();
        Node currPos = vehicle.getCurrPos();

        if (request.getType() == Request.RequestType.CHARGE) {
            return LIMIT;
        }

        int tMax = request.getTMax();
        int tCurr = state.getTime();
        int travelTime = instance.calculateTravelTime(currPos.calcDist(request.getPickup()));

        return tMax - tCurr - travelTime;
    }
}
