package gpdarp.gp.terminal.feature;

import gpdarp.core.Instance;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns the average travel time from the request's pickup point to the
 * pickup and dropoff points of all (other) waiting requests.
 *
 * @author William Huang
 */
public class RequestCrowdedness extends FeatureGPNode {
    public RequestCrowdedness() {
        super();
        name = "CRD";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Request request = calcPriorityProblem.getRequest();
        DecisionProcessState state = calcPriorityProblem.getState();
        Instance instance = state.getInstance();

        Node pickup = request.getPickup();
        List<Request> requests = new ArrayList<>(state.getWaitingList());
        requests.remove(request);

        int averageDistance;
        if (requests.isEmpty()) {
            averageDistance = 0;
        }
        else {
            averageDistance = requests.stream()
                    .map(r -> (pickup.calcDist(r.getPickup()) + pickup.calcDist(r.getDropoff())) / 2)
                    .reduce(0, Integer::sum) / requests.size();
        }

        return instance.calculateTravelTime(averageDistance);
    }
}
