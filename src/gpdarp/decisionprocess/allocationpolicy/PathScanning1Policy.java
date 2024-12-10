package gpdarp.decisionprocess.allocationpolicy;

import gpdarp.core.*;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;

/**
 * The path scanning 1 policy first selects the nearest vehicle.
 * Then, among multiple nearest vehicles, it minimises the percentage of remaining capacity.
 *
 * @author gphhucarp, William Huang
 */
public class PathScanning1Policy extends AllocationPolicy {
    // a sufficiently large coefficient to guarantee the priority of nearest vehicle
    public static final double ALPHA = 10000;

    public PathScanning1Policy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"PS1\"";
    }

    public PathScanning1Policy(TieBreaker tieBreaker) {
        this(new FeasiblePoolFilter(), tieBreaker);
    }

    public PathScanning1Policy() {
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
        double percentCapacity = (double) candidate.getRemainingCapacity() / candidate.getCapacity();

        return ALPHA * distanceToPickup + percentCapacity;
    }
}
