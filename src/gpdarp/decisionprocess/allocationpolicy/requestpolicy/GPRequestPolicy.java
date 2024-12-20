package gpdarp.decisionprocess.allocationpolicy.requestpolicy;

import ec.gp.GPTree;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.gp.CalcPriorityProblem;
import gputils.DoubleData;

/**
 * A GP-evolved request allocation policy.
 *
 * @author William Huang
 */
public class GPRequestPolicy extends RequestPolicy {
    private GPTree gpTree;

    public GPRequestPolicy(GPTree gpTree) {
        super();
        name = "\"GPRequestPolicy\"";
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
    public double priority(Request candidate, DecisionProcessState state, Vehicle vehicle) {
        CalcPriorityProblem calcPrioProb = new CalcPriorityProblem(vehicle, candidate, state);
        DoubleData tmp = new DoubleData();
        gpTree.child.eval(null, 0, tmp, null, null, calcPrioProb);

        return tmp.value;
    }
}