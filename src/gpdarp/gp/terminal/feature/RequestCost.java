package gpdarp.gp.terminal.feature;

import gpdarp.core.Instance;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns the expected cost for serving the request.
 *
 * @author William Huang
 */
public class RequestCost extends FeatureGPNode {
    public RequestCost() {
        super();
        name = "COST";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        Request request = calcPriorityProblem.getRequest();
        DecisionProcessState state = calcPriorityProblem.getState();
        Instance instance = state.getInstance();
        Node start = vehicle.getCurrPos();
        Node pickup = request.getPickup();
        Node dropoff = request.getDropoff();

        int serviceLength = start.calcDist(pickup) + pickup.calcDist(dropoff);
        int returnLength = dropoff.calcDist(instance.findClosestStation(dropoff));

        return instance.calculateTravelTime(serviceLength + returnLength);
    }
}
