package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Node;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

public class ReactiveDropoffEvent extends DecisionProcessEvent {
    Node node;
    Vehicle vehicle;

    public ReactiveDropoffEvent(int time, Node node, Vehicle vehicle) {
        super(time);
        this.node = node;
        this.vehicle = vehicle;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        vehicle.dropoff(node);

        Node waiting = node.clone();
        Node destination = vehicle.updateArcFromPlannedRoute();

        if (destination != null) {
            switch (destination.getType()) {
                case PICKUP -> decisionProcess.addEvent(
                        new ReactivePickupEvent(destination.getEta(), destination, vehicle));

                case DROPOFF -> decisionProcess.addEvent(
                        new ReactiveDropoffEvent(destination.getEta(), destination, vehicle));
            }
        }
        else {
            waiting.setEta(waiting.getEta() + vehicle.getServeTime());
            vehicle.setCurrPos(waiting);

            // TODO: set the condition for whether a vehicle waits where it is or decides to charge
            if (true) {

            }
        }

    }
}
