package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.representation.route.Route;

import java.util.Map;

/**
 * This event represents when a new request has been received.
 * During this event, the request is allocated to a vehicle, and the vehicle's route is updated.
 * If the vehicle is not currently moving, a new event is invoked to move to the next point in the route.
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
        Vehicle vehicle = vehicleAllocation(decisionProcess, request);

        if (vehicle != null) {
            if (!vehicle.isMoving()) {
                vehicle.setCurrPos(null);
                Node destination = vehicle.updateArcFromPlannedRoute();
                decisionProcess.addEvent(new ReactivePickupEvent(destination.getEta(), destination, vehicle));
            }
        }
    }
}
