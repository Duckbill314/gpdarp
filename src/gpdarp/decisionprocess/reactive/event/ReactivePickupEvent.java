package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Node;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

/**
 * This event represents arrival of a vehicle to a pickup point in its route.
 * During this event, the node is visited, the vehicle state is updated,
 * and a new event is invoked to move to the next point in the route.
 *
 * @author William Huang
 */
public class ReactivePickupEvent extends DecisionProcessEvent {
    Node node;
    Vehicle vehicle;

    public ReactivePickupEvent(int time, Node node, Vehicle vehicle) {
        super(time);
        this.node = node;
        this.vehicle = vehicle;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        vehicle.pickup(node);

        Node destination = vehicle.updateArcFromPlannedRoute();
        switch (destination.getType()) {
            case PICKUP -> decisionProcess.addEvent(
                    new ReactivePickupEvent(destination.getEta(), destination, vehicle));

            case DROPOFF -> decisionProcess.addEvent(
                    new ReactiveDropoffEvent(destination.getEta(), destination, vehicle));
        }
    }
}
