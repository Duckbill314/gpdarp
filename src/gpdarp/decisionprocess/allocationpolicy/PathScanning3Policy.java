package gpdarp.decisionprocess.allocationpolicy;

import gpdarp.core.*;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;

/**
 * The path scanning 3 policy first selects the nearest vehicle.
 * Then, among multiple nearest vehicles, it minimises the percentage of remaining charge.
 *
 * @author gphhucarp, William Huang
 */

public class PathScanning3Policy extends AllocationPolicy {
    // a sufficiently large coefficient to guarantee the priority of nearest vehicle
    public static final double ALPHA = 10000;

    public PathScanning3Policy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"PS3\"";
    }

    public PathScanning3Policy(TieBreaker tieBreaker) { this(new FeasiblePoolFilter(), tieBreaker); }

    public PathScanning3Policy() {
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
        double percentCharge = candidate.getChargeState() / candidate.getChargeMax();

        return ALPHA * distanceToPickup + percentCharge;
    }
}
