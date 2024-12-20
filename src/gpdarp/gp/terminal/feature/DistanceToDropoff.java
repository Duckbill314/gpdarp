package gpdarp.gp.terminal.feature;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

public class DistanceToDropoff extends FeatureGPNode {
    public DistanceToDropoff() {
        super();
        name = "DTD";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        Request request = calcPriorityProblem.getRequest();

        return vehicle.getCurrPos().calcDist(request.getDropoff());
    }
}
