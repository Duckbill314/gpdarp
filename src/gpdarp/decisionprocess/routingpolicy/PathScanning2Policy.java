package gpdarp.decisionprocess.routingpolicy;

import gpdarp.core.Arc;
import gpdarp.core.Graph;
import gpdarp.core.Instance;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.RoutingPolicy;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.decisionprocess.poolfilter.ExpFeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;

/**
 * The path scanning 2 policy first selects the nearest neighbours.
 * Among multiple nearest neighbours,
 * it minimises the cost to depot
 */

public class PathScanning2Policy extends RoutingPolicy {
    // a sufficiently large coefficient to guarantee the priority of cost from here
    public static final double ALPHA = 10000;

    public PathScanning2Policy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"PS2\"";
    }

    public PathScanning2Policy(TieBreaker tieBreaker) {
        this(new ExpFeasiblePoolFilter(), tieBreaker);
    }

    public PathScanning2Policy() {
        this(new SimpleTieBreaker());
    }

    @Override
    public double priority(Arc candidate, NodeSeqRoute route, DecisionProcessState state) {
        Instance instance = state.getInstance();
        Graph graph = instance.getGraph();
        double costFromHere = graph.getEstDistance(route.currPos(), candidate.getFrom());
        double costToDepot = graph.getEstDistance(candidate.getTo(), instance.getDepot());

        return ALPHA * costFromHere + costToDepot;
    }
}
