package gpdarp.gp.terminal.feature;

import gpdarp.core.Instance;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * Returns the distance between the vehicle and the nearest charging station at the point that the vehicle will be
 * after serving the request. Of course, for charging requests, this value will be 0.
 *
 * @author William Huang
 */
public class FutureDistanceFromStation extends FeatureGPNode {
    public FutureDistanceFromStation() {
        super();
        name = "FDFS";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Request request = calcPriorityProblem.getRequest();
        DecisionProcessState state = calcPriorityProblem.getState();
        Instance instance = state.getInstance();
        Node futPos = request.getDropoff();

        double value = 0;

        switch (request.getType()) {
            case REQUEST -> value = futPos.calcDist(instance.findClosestStation(futPos));
        }

        return value;
    }
}