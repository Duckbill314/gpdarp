package gpdarp.gp.terminal.feature;

import gpdarp.core.Instance;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

public class CurrentDistanceFromStation extends FeatureGPNode {
    public CurrentDistanceFromStation() {
        super();
        name = "CDFS";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        Request request = calcPriorityProblem.getRequest();
        DecisionProcessState state = calcPriorityProblem.getState();
        Instance instance = state.getInstance();
        Node currPos = vehicle.getCurrPos();

        double value = 0;

        switch (request.getType()) {
            case REQUEST -> value = currPos.calcDist(instance.findClosestStation(currPos));

            case CHARGE -> value = currPos.calcDist(request.getDropoff());
        }

        return value;
    }
}
