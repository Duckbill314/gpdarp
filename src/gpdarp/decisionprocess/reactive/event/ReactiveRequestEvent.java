package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.*;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This event represents when a new request has been received.
 * During this event, the request is allocated to a vehicle, and the vehicle's route is updated.
 * It may also be expanded upon with existing waiting requests.
 * Then, a new event is invoked to move to the next point in the route.
 * If no vehicle allocation is made, instead, the request is added to a waiting queue.
 * It is possible that no allocation is made because the request's pickup time is too far in the future.
 * Thus, all vehicles that rejected the request for this reason will give the request the chance to be accepted
 * closer to its actual pickup time.
 *
 * @author William Huang
 */
public class ReactiveRequestEvent extends DecisionProcessEvent {
    Request request;

    public ReactiveRequestEvent(int time, Request request) {
        super("ReactiveRequestEvent", time);
        this.request = request;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        Vehicle vehicle = vehicleAllocation(decisionProcess, request);

        if (vehicle != null) {
            constructiveHeuristic(decisionProcess, vehicle);
            vehicle.setCurrPos(null);
            Node destination = vehicle.updateArcFromPlannedRoute();
            decisionProcess.addEvent(new ReactivePickupEvent(destination.getArrivalTime(), destination, vehicle));
        }
        else {
            Instance instance = decisionProcess.getState().getInstance();
            List<Vehicle> vehicles = new ArrayList<>(instance.getVehicles());
            vehicles.removeIf(v -> v.isBusy());
            for (Vehicle v : vehicles) {
                Arc arc = new Arc(v.getCurrPos(), request.getPickup());
                int travelTime = instance.calculateTravelTime(arc.length());
                int softSlack = request.getTEarly() - time - travelTime;
                if (softSlack > 0) {
                    decisionProcess.addEvent(new ReactiveRepeatEvent(time + softSlack, request));
                }
            }
        }
    }
}
