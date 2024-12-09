package gpdarp.decisionprocess.allocationpolicy;

import gpdarp.core.Arc;
import gpdarp.core.Graph;
import gpdarp.core.Instance;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.decisionprocess.poolfilter.ExpFeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;

/**
 * The path scanning 3 policy first selects the nearest neighbours.
 * Among multiple nearest neighbours,
 * it maximises the yield = demand/servCost
 */

public class PathScanning3Policy extends AllocationPolicy {
    // a sufficiently large coefficient to guarantee the priority of cost from here
    public static final double ALPHA = 10000;

    public PathScanning3Policy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"PS3\"";
    }

    public PathScanning3Policy(TieBreaker tieBreaker) {
        this(new ExpFeasiblePoolFilter(), tieBreaker);
    }

    public PathScanning3Policy() {
        this(new SimpleTieBreaker());
    }

    @Override
    public double priority(Vehicle candidate, NodeSeqRoute route, DecisionProcessState state) {
        Instance instance = state.getInstance();
        Graph graph = instance.getGraph();
        double costFromHere = graph.getEstDistance(route.currPos(), candidate.getFrom());
        double yield = state.getInstance().getActDemand(candidate) / candidate.getServeCost();

        return ALPHA * costFromHere - yield;
    }
}
