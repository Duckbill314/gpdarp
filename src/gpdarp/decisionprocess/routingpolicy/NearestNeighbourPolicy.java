package gpdarp.decisionprocess.routingpolicy;

import gpdarp.core.Arc;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.decisionprocess.poolfilter.ExpFeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.RoutingPolicy;

/**
 * The nearest neighbour policy always selects the nearest neighbour.
 * The priority is set to the distance from the current node to the head node of the candidate.
 * If there are multiple nearest neighbours, it randomly choose one.
 *
 * Created by gphhucarp on 29/08/17.
 */
public class NearestNeighbourPolicy extends RoutingPolicy {

    public NearestNeighbourPolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"NN\"";
    }

    public NearestNeighbourPolicy(TieBreaker tieBreaker) {
        this(new ExpFeasiblePoolFilter(), tieBreaker);
    }

    public NearestNeighbourPolicy() {
        this(new SimpleTieBreaker());
    }

    @Override
    public double priority(Arc candidate, NodeSeqRoute route, DecisionProcessState state) {
        return state.getInstance().getGraph().getEstDistance(route.currPos(), candidate.getFrom());
    }
}
