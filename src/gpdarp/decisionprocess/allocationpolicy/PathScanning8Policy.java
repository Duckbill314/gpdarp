package gpdarp.decisionprocess.allocationpolicy;

import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.AllocationPolicy;

/**
 * The path scanning 8 policy first selects the nearest vehicles.
 * Then, among multiple nearest vehicles, it maximises the length of the historical route.
 *
 * @author gphhucarp, William Huang
 */

public class PathScanning8Policy extends AllocationPolicy {
    // a sufficiently large coefficient to guarantee the priority of nearest vehicle
    public static final double ALPHA = 10000;

    public PathScanning8Policy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"PS8\"";
    }

    public PathScanning8Policy(TieBreaker tieBreaker) {
        this(new FeasiblePoolFilter(), tieBreaker);
    }

    public PathScanning8Policy() {
        this(new SimpleTieBreaker());
    }

    @Override
    public double priority(Vehicle candidate, Request request, DecisionProcessState state) {
        Node pos;
        if (candidate.getCurrArc() == null) {
            pos = candidate.getCurrPos();
        }
        else {
            pos = candidate.getCurrArc().to();
        }
        int distanceToPickup = pos.calcDist(request.pickup());
        int routeLength = candidate.getHistoricalRoute().getLength();

        return ALPHA * distanceToPickup - routeLength;
    }
}
