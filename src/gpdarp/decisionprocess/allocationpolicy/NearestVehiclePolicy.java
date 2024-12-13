package gpdarp.decisionprocess.allocationpolicy;

import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.decisionprocess.poolfilter.FeasiblePoolFilter;
import gpdarp.decisionprocess.tiebreaker.SimpleTieBreaker;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;

/**
 * The nearest vehicle policy selects the vehicle whose current position (if it is stationary) or whose current
 * destination (if it is moving) is closest to the request pickup point.
 * The priority is set to the length between the two aforementioned points.
 *
 * @author gphhucarp, William Huang
 */
public class NearestVehiclePolicy extends AllocationPolicy {
    public NearestVehiclePolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"NV\"";
    }

    public NearestVehiclePolicy() {
        this(new FeasiblePoolFilter(), new SimpleTieBreaker());
    }

    @Override
    public double priority(Vehicle candidate, DecisionProcessState state, Request request) {
        Node pos;
        if (candidate.getCurrArc() == null) {
            pos = candidate.getCurrPos();
        }
        else {
            pos = candidate.getCurrArc().to();
        }
        return pos.calcDist(request.getPickup());
    }
}
