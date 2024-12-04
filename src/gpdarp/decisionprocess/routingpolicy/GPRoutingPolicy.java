package gpdarp.decisionprocess.routingpolicy;

import ec.gp.GPTree;
import gpdarp.core.Arc;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.RoutingPolicy;
import gpdarp.decisionprocess.poolfilter.IdentityPoolFilter;
import gpdarp.gp.CalcPriorityProblem;
import gputils.DoubleData;

/**
 * A GP-evolved routing policy.
 *
 * Created by gphhucarp on 30/08/17.
 */
public class GPRoutingPolicy extends RoutingPolicy {

    private GPTree gpTree;

    public GPRoutingPolicy(PoolFilter poolFilter, GPTree gpTree) {
        super(poolFilter);
        name = "\"GPRoutingPolicy\"";
        this.gpTree = gpTree;
    }

    public GPRoutingPolicy(GPTree gpTree) {
        this(new IdentityPoolFilter(), gpTree);
    }

    public GPTree getGPTree() {
        return gpTree;
    }

    public void setGPTree(GPTree gpTree) {
        this.gpTree = gpTree;
    }

    @Override
    public double priority(Arc candidate, NodeSeqRoute route, DecisionProcessState state) {
        CalcPriorityProblem calcPrioProb =
                new CalcPriorityProblem(candidate, route, state);

        DoubleData tmp = new DoubleData();
        gpTree.child.eval(null, 0, tmp, null, null, calcPrioProb);

        return tmp.value;
    }
}
