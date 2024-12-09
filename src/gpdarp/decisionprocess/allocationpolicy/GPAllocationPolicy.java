package gpdarp.decisionprocess.allocationpolicy;

import ec.gp.GPTree;
import gpdarp.core.Arc;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.poolfilter.IdentityPoolFilter;
import gpdarp.gp.CalcPriorityProblem;
import gputils.DoubleData;

/**
 * A GP-evolved routing policy.
 *
 * Created by gphhucarp on 30/08/17.
 */
public class GPAllocationPolicy extends AllocationPolicy {

    private GPTree gpTree;

    public GPAllocationPolicy(PoolFilter poolFilter, GPTree gpTree) {
        super(poolFilter);
        name = "\"GPAllocationPolicy\"";
        this.gpTree = gpTree;
    }

    public GPAllocationPolicy(GPTree gpTree) {
        this(new IdentityPoolFilter(), gpTree);
    }

    public GPTree getGPTree() {
        return gpTree;
    }

    public void setGPTree(GPTree gpTree) {
        this.gpTree = gpTree;
    }

    @Override
    public double priority(Vehicle candidate, NodeSeqRoute route, DecisionProcessState state) {
        CalcPriorityProblem calcPrioProb =
                new CalcPriorityProblem(candidate, route, state);

        DoubleData tmp = new DoubleData();
        gpTree.child.eval(null, 0, tmp, null, null, calcPrioProb);

        return tmp.value;
    }
}
