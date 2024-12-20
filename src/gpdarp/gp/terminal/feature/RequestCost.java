package gpdarp.gp.terminal.feature;

import gpdarp.core.Instance;
import gpdarp.core.Request;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

public class RequestCost extends FeatureGPNode {
    public RequestCost() {
        super();
        name = "COST";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Request request = calcPriorityProblem.getRequest();
        DecisionProcessState state = calcPriorityProblem.getState();
        Instance instance = state.getInstance();

        double value = 0;

        switch (request.getType()) {
            case REQUEST -> value = request.getPickup().calcDist(request.getDropoff());
        }

        return instance.calculateTravelTime((int) value);
    }
}
