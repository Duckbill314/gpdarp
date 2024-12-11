package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Node;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

public class ReactivePickupEvent extends DecisionProcessEvent {
    Node node;

    public ReactivePickupEvent(int time, Node node) {
        super(time);
        this.node = node;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {

    }
}
