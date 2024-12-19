package gpdarp.decisionprocess.allocationpolicy.requestpolicy;

import gpdarp.core.Vehicle;
import gpdarp.core.WaitingRequest;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.RequestPolicy;

/**
 * The nearest request policy selects the request whose pickup point (or for a charging event, station location)
 * is closest to the vehicle's current position.
 * The priority is set to the length between the two aforementioned points.
 *
 * @author gphhucarp, William Huang
 */
public class NearestRequestPolicy extends RequestPolicy {
    // A priority threshold must be set so that the allocator has the option of choosing no action
    private static final double THRESHOLD = 200;

    public NearestRequestPolicy() {
        super();
        name = "\"NearestRequestPolicy\"";
    }

    @Override
    public double priority(WaitingRequest candidate, DecisionProcessState state, Vehicle vehicle) {
        double priority = 0;
        switch (candidate.getType()) {
            case REQUEST -> priority = vehicle.getCurrPos().calcDist(candidate.getPickup());

            case CHARGE -> priority = vehicle.getCurrPos().calcDist(candidate.getDropoff());
        }
        return priority - THRESHOLD;
    }
}
