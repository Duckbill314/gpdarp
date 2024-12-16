package gpdarp.decisionprocess.allocationpolicy.requestpolicy;

import ec.gp.GPTree;
import gpdarp.core.IdleRequest;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.representation.route.Route;
import gputils.DoubleData;

import java.util.Map;

public class GPRequestPolicy extends RequestPolicy {
    private GPTree gpTree;

    public GPRequestPolicy(PoolFilter poolFilter, GPTree gpTree) {
        super(poolFilter);
        name = "\"GPRequestPolicy\"";
        this.gpTree = gpTree;
    }

    public GPRequestPolicy(GPTree gpTree) {
        this(new FeasiblePoolFilter(), gpTree);
    }

    public GPTree getGPTree() {
        return gpTree;
    }

    public void setGPTree(GPTree gpTree) {
        this.gpTree = gpTree;
    }

    @Override
    public double priority(Map.Entry<IdleRequest, Route> candidate, DecisionProcessState state, Vehicle vehicle) {
        CalcPriorityProblem calcPrioProb = new CalcPriorityProblem(candidate, state, vehicle);
        DoubleData tmp = new DoubleData();
        gpTree.child.eval(null, 0, tmp, null, null, calcPrioProb);

        return tmp.value;
    }
}