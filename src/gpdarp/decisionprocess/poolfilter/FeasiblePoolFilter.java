package gpdarp.decisionprocess.poolfilter;

import gpdarp.core.*;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.representation.route.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Filters for the candidate vehicles and their optimal route from the pool by selecting only the ones that are
 * expected to be feasible. Feasibility is accounted for during route calculation.
 *
 * @author William Huang
 */
public class FeasiblePoolFilter extends PoolFilter {
    @Override
    public List<Vehicle> filterVehicles(DecisionProcessState state, Request request) {
        Instance instance = state.getInstance();
        List<Vehicle> pool = new ArrayList<>(instance.getVehicles());

        pool.removeIf(Vehicle::isBusy);
        pool.removeIf(vehicle -> demandConstraintViolation(vehicle, request));
        pool.removeIf(vehicle -> chargeConstraintViolation(instance, vehicle, request));

        return pool;
    }

    @Override
    public List<WaitingRequest> filterRequests(Vehicle vehicle, DecisionProcessState state,
                                               List<WaitingRequest> requests) {

        Instance instance = state.getInstance();
        List<WaitingRequest> pool = new ArrayList<>();

        for (WaitingRequest request : requests) {
            switch (request.getType()) {
                case CHARGE -> pool.add(request);

                case REQUEST -> {
                    if (!demandConstraintViolation(vehicle, request) &&
                            !chargeConstraintViolation(instance, vehicle, request)) {
                        pool.add(request);
                    }
                }
            }
        }
        return pool;
    }

    /**
     * For a potential request, check whether a vehicle will have enough capacity to serve it.
     *
     * @param vehicle the vehicle servicing the request.
     * @param request the request to be served.
     *
     * @return whether violation occurred.
     */
    public boolean demandConstraintViolation(Vehicle vehicle, Request request) {
        return (request.getDemand() > vehicle.getCapacity());
    }

    /**
     * For a potential request, check whether a vehicle will have enough charge to return to a charging station
     * after serving it.
     *
     * @param instance the problem instance.
     * @param vehicle the vehicle servicing the request.
     * @param request the request to be served.
     *
     * @return whether violation occurred.
     */
    public boolean chargeConstraintViolation(Instance instance, Vehicle vehicle, Request request) {
        Node start = vehicle.getCurrPos();
        Node pickup = request.getPickup();
        Node dropoff = request.getDropoff();
        int serviceLength = start.calcDist(pickup) + pickup.calcDist(dropoff);
        int returnLength = dropoff.calcDist(instance.findClosestStation(dropoff));
        double estimatedChargeState = vehicle.estimateDepletion(serviceLength + returnLength);
        return (estimatedChargeState < 0);
    }
}
