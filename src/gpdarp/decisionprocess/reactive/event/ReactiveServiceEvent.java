package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.WaitingRequest;
import gpdarp.core.Node;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;
import gpdarp.decisionprocess.DecisionProcessState;

/**
 * This event represents completion of request service.
 * During this event, the vehicle may choose to accept a request from the waiting list of requests, or go to charge.
 * If no action is taken, it simply stays where it is until the next time it accepts a fresh request.
 *
 * @author William Huang
 */
public class ReactiveServiceEvent extends DecisionProcessEvent {
    Node node;
    Vehicle vehicle;

    public ReactiveServiceEvent(int time, Node node, Vehicle vehicle) {
        super(time);
        this.node = node;
        this.vehicle = vehicle;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        vehicle.setCurrPos(node);
        DecisionProcessState state = decisionProcess.getState();
        WaitingRequest request = requestAllocation(decisionProcess, vehicle, node, true);

        if (request != null) {
            switch (request.getType()) {
                case REQUEST -> {
                    decisionProcess.removeWaiting(request);
                    Node nextIdleNode = vehicle.serveRequest(state.getInstance(), request, node);
                    decisionProcess.addEvent(new ReactiveServiceEvent(nextIdleNode.getTime(), nextIdleNode, vehicle));
                }
                case CHARGE -> {
                    Node nextIdleNode = vehicle.charge(state.getInstance(), request);
                    decisionProcess.addEvent(new ReactiveChargeEvent(nextIdleNode.getTime(), nextIdleNode, vehicle));
                }
            }
        }
    }
}
