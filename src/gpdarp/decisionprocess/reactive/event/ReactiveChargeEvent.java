package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;
import gpdarp.decisionprocess.DecisionProcessState;

/**
 * This event represents the moment a vehicle has finished charging.
 * During this event, the vehicle will be given one final opportunity to accept a waiting request.
 * If no action is taken, it simply stays where it is until the next time it accepts a fresh request.
 *
 * @author William Huang
 */
public class ReactiveChargeEvent extends DecisionProcessEvent {
    Node node;
    Vehicle vehicle;

    public ReactiveChargeEvent(int time, Node node, Vehicle vehicle) {
        super("ReactiveChargeEvent", time);
        this.node = node;
        this.vehicle = vehicle;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        vehicle.setCurrPos(node);
        DecisionProcessState state = decisionProcess.getState();
        Request request = requestAllocation(decisionProcess, vehicle, node, false);

        if (request != null) {
            decisionProcess.removeWaiting(request);
            Node nextIdleNode = vehicle.serveRequest(state.getInstance(), request, node);
            decisionProcess.addEvent(new ReactiveServiceEvent(nextIdleNode.getTime(), nextIdleNode, vehicle));
        }
    }
}
