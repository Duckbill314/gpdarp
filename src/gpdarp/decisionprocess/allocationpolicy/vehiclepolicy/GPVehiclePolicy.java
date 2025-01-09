package gpdarp.decisionprocess.allocationpolicy.vehiclepolicy;

import ec.gp.GPTree;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.representation.route.Route;
import gputils.DoubleData;

/**
 * A GP-evolved vehicle allocation policy.
 *
 * @author gphhucarp
 */
public class GPVehiclePolicy extends VehiclePolicy {
    private GPTree gpTree;

    public GPVehiclePolicy(GPTree gpTree) {
        super();
        name = "\"GPVehiclePolicy\"";
        this.gpTree = gpTree;
    }

    // Getters
    public GPTree getGPTree() {
        return gpTree;
    }

    // Setters
    public void setGPTree(GPTree gpTree) {
        this.gpTree = gpTree;
    }

    @Override
    public double priority(Vehicle candidate, Request request, Route route, DecisionProcessState state) {
        CalcPriorityProblem calcPrioProb = new CalcPriorityProblem(candidate, request, route, state);
        DoubleData tmp = new DoubleData();
        gpTree.child.eval(null, 0, tmp, null, null, calcPrioProb);

        return tmp.value;
    }
}
