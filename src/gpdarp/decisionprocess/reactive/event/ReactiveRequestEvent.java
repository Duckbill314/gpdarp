package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;
import gpdarp.decisionprocess.DecisionProcessState;

/**
 * This event represents when a new request has been received.
 * During this event, the request is allocated to a vehicle, and the vehicle services the request.
 * A vehicle may only accept a request if it is not currently serving another request.
 * If no vehicle allocation is made, instead, the request is added to a waiting queue.
 *
 * @author William Huang
 */
public class ReactiveRequestEvent extends DecisionProcessEvent {
    Request request;

    public ReactiveRequestEvent(int time, Request request) {
        super(time);
        this.request = request;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        DecisionProcessState state = decisionProcess.getState();
        Vehicle vehicle = vehicleAllocation(decisionProcess, request);

        if (vehicle != null) {
            Node startPoint = vehicle.getCurrPos().idleClone(time);
            Node nextIdleNode = vehicle.serveRequest(state.getInstance(), request, startPoint);
            decisionProcess.addEvent(new ReactiveServiceEvent(nextIdleNode.getTime(), nextIdleNode, vehicle));
        }
    }
}
