package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.*;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;
import gpdarp.representation.route.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This event will give the requests still waiting a final chance to be allocated.
 * There are two main reasons why a request may still be in the waiting list:
 * - The priority values are faultily above the threshold.
 * - The vehicles faultily left themselves without enough charge.
 * During last call, the threshold is ignored and vehicles are forcefully sent to charge if necessary.
 * All feasibility constraints still apply.
 *
 * @author William Huang
 */
public class ReactiveLastCallEvent extends DecisionProcessEvent {
    public ReactiveLastCallEvent(int time) {
        super("ReactiveLastCallEvent", time);
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        List<Request> waitingList = decisionProcess.getWaitingList();
        List<Request> lastCalls = new ArrayList<>(waitingList);
        for (Request request : lastCalls) {
            if (waitingList.contains(request)) {
                Vehicle vehicle = vehicleAllocation(decisionProcess, request, true);
                if (vehicle != null) {
                    constructiveHeuristic(decisionProcess, vehicle, true);
                    vehicle.setCurrPos(null);
                    Node destination = vehicle.updateArcFromPlannedRoute();
                    decisionProcess.addEvent(new ReactivePickupEvent(destination.getArrivalTime(), destination, vehicle));
                }
            }
        }

        if (waitingList.size() == lastCalls.size()) {
            Instance instance = decisionProcess.getState().getInstance();
            List<Vehicle> vehicles = instance.getVehicles();
            for (Vehicle vehicle : vehicles) {
                Node currPos = vehicle.getCurrPos();
                Node station = instance.findClosestStation(currPos).clone();
                Request request = new Request(currPos.getDepartureTime(), currPos, station);

                List<Node> chargeRoute = new ArrayList<>(Arrays.asList(request.getPickup(), request.getDropoff()));
                List<Request> chargeRequest = new ArrayList<>(List.of(request));
                Route route = Route.buildFromNodeList(chargeRoute, chargeRequest);

                vehicle.updateRoute(decisionProcess.getState(), route);
                vehicle.updateArcFromPlannedRoute();
                vehicle.charge();
                decisionProcess.addEvent(new ReactiveLastChargeEvent(vehicle.getCurrPos().getDepartureTime(), vehicle));
            }
        }
    }
}
