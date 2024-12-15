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
 * During this event, the request is allocated to a vehicle.
 * If no vehicle allocation is made, the request is instead added to a waiting queue.
 * If the vehicle is currently idle, a new event is invoked to put it into motion.
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
        Map.Entry<Vehicle, Route> allocation = decisionProcess.getAllocationPolicy().next(state, request);

        if (allocation == null) {
            decisionProcess.addWaiting(request);
        }
        else {
            Vehicle vehicle = allocation.getKey();
            Route route = allocation.getValue();
            vehicle.allocate(request);
            vehicle.updateRoute(state, route);

            if (!vehicle.isMoving()) {
                vehicle.setCurrPos(null);
                Node destination = vehicle.updateArcFromPlannedRoute();
                decisionProcess.addEvent(new ReactivePickupEvent(destination.getEta(), destination, vehicle));
            }
        }
    }
}
