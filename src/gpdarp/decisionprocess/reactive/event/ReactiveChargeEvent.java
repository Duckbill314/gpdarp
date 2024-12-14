package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Node;
import gpdarp.core.Station;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

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
