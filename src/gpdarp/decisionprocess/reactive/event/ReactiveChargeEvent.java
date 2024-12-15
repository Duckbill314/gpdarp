package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

/**
 * This event represents a vehicle in its charging state.
 * During this event, the vehicle will move to a charging station, and then replenish its charge.
 * For the entire duration of this event, the vehicle is still able to accept incoming requests, but it is considered
 * "dead" and cannot serve those requests until its "dead" time is over.
 *
 * @author William Huang
 */
public class ReactiveChargeEvent extends DecisionProcessEvent {
    Vehicle vehicle;

    public ReactiveChargeEvent(int time, Vehicle vehicle) {
        super(time);
        this.vehicle = vehicle;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        vehicle.charge(decisionProcess.getState().getInstance());
    }
}
