package gpdarp.decisionprocess.allocationpolicy;

import ec.gp.GPTree;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.representation.route.Route;
import gputils.DoubleData;

import java.util.Map;

/**
 * A GP-evolved routing policy.
 *
 * @author gphhucarp
 */
public class GPAllocationPolicy extends AllocationPolicy {
    private GPTree gpTree;

    public GPAllocationPolicy(PoolFilter poolFilter, GPTree gpTree) {
        super(poolFilter);
        name = "\"GPAllocationPolicy\"";
        this.gpTree = gpTree;
    }

    public GPAllocationPolicy(GPTree gpTree) {
        this(new FeasiblePoolFilter(), gpTree);
    }

    public GPTree getGPTree() {
        return gpTree;
    }

    public void setGPTree(GPTree gpTree) {
        this.gpTree = gpTree;
    }

    @Override
    public double priority(Map.Entry<Vehicle, Route> candidate, DecisionProcessState state, Request request) {
        CalcPriorityProblem calcPrioProb = new CalcPriorityProblem(candidate, request, state);
        DoubleData tmp = new DoubleData();
        gpTree.child.eval(null, 0, tmp, null, null, calcPrioProb);

        return tmp.value;
    }
}
