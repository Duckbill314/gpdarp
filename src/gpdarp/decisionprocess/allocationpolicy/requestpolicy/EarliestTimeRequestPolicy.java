package gpdarp.decisionprocess.allocationpolicy.requestpolicy;

import gpdarp.core.Vehicle;
import gpdarp.core.WaitingRequest;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.representation.route.Route;

import java.util.Map;

/**
 * The earliest time policy selects the request that was received the earliest.
 * Charging requests are logged with the vehicle's next available time.
 *
 * @author William Huang
 */
public class EarliestTimeRequestPolicy extends RequestPolicy {
    public EarliestTimeRequestPolicy() {
        super();
        name = "\"EarliestTimeRequestPolicy\"";
    }

    @Override
    public double priority(Map.Entry<WaitingRequest, Route> candidate, DecisionProcessState state, Vehicle vehicle) {
        return candidate.getKey().getTRec();
    }
}
