package gpdarp.gp.terminal.feature;

import gpdarp.core.Instance;
import gpdarp.core.Node;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.representation.route.EphemeralRoute;

/**
 * Returns the travel time between the last position in the vehicle's route and the nearest charging station.
 *
 * @author William Huang
 */
public class TimeToStation extends FeatureGPNode {
    public TimeToStation() {
        super();
        name = "TVC";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        EphemeralRoute route = calcPriorityProblem.getRoute();
        DecisionProcessState state = calcPriorityProblem.getState();
        Instance instance = state.getInstance();
        Node pos = route.getEndpoint();
        return instance.calculateTravelTime(pos.calcDist(instance.findClosestStation(pos)));
    }
}
