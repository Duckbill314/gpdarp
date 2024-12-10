package gpdarp.decisionprocess.allocationpolicy;

import gpdarp.core.*;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;

/**
 * The path scanning 4 policy first selects the nearest vehicles.
 * Then, among multiple nearest vehicles, it maximises the percentage of remaining charge.
 *
 * @author gphhucarp, William Huang
 */

public class PathScanning4Policy extends AllocationPolicy {
    // a sufficiently large coefficient to guarantee the priority of nearest vehicle
    public static final double ALPHA = 10000;

    public PathScanning4Policy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"PS4\"";
    }

    public PathScanning4Policy(TieBreaker tieBreaker) {
        this(new FeasiblePoolFilter(), tieBreaker);
    }

    public PathScanning4Policy() {
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

        return ALPHA * distanceToPickup - percentCharge;
    }
}
