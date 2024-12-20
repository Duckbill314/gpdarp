package gpdarp.gp.terminal.feature;

import gpdarp.core.Instance;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

public class ExpectedPenalty extends FeatureGPNode {
    public ExpectedPenalty() {
        super();
        name = "PEN";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        Request request = calcPriorityProblem.getRequest();
        DecisionProcessState state = calcPriorityProblem.getState();
        Instance instance = state.getInstance();
        Node currPos = vehicle.getCurrPos();

        return 0; // TODO
    }
}
