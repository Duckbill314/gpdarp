package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Node;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

/**
 * This event represents a vehicle in its waiting state after serving its route.
 * During this event, the vehicle will try to accept a request from the waiting list.
 * If no request is accepted, the vehicle instead goes to charge.
 *
 * @author William Huang
 */
public class ReactiveIdleEvent extends DecisionProcessEvent {
    Node node;
    Vehicle vehicle;

    public ReactiveIdleEvent(int time, Node node, Vehicle vehicle) {
        super(time);
        this.node = node;
        this.vehicle = vehicle;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {

    }
}
