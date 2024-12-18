package gpdarp.decisionprocess.poolfilter;

import gpdarp.core.*;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.representation.RequestPool;
import gpdarp.representation.VehiclePool;
import gpdarp.representation.route.Route;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Filters for the candidate vehicles and their optimal route from the pool by selecting only the ones that are
 * expected to be feasible. Feasibility is accounted for during route calculation.
 *
 * @author William Huang
 */
public class FeasiblePoolFilter extends PoolFilter {
    @Override
    public VehiclePool filterVehicles(DecisionProcessState state, Request request) {
        Instance instance = state.getInstance();
        List<Vehicle> vehicles = new ArrayList<>(instance.getVehicles());

        VehiclePool vehiclePool = new VehiclePool();

        for (Vehicle vehicle : vehicles) {
            List<Request> requests = new ArrayList<>(vehicle.getRequests());
            requests.add(request);
            Route route = recalculate(vehicle, state, requests);

            if (route != null) {
                vehiclePool.put(vehicle, route);
            }
        }

        return vehiclePool;
    }

    @Override
    public RequestPool filterRequests(Vehicle vehicle, DecisionProcessState state, List<WaitingRequest> requests) {
        RequestPool requestPool = new RequestPool();
        for (WaitingRequest request : requests) {
            switch (request.getType()) {
                case CHARGE -> requestPool.put(request, null);

                case REQUEST -> {
                    List<Request> vehicleRequests = new ArrayList<>(vehicle.getRequests());
                    vehicleRequests.add(request);
                    Route route = recalculate(vehicle, state, vehicleRequests);

                    if (route != null) {
                        requestPool.put(request, route);
                    }
                }
            }
        }

        return requestPool;
    }

    /**
     * Based on the unvisited nodes of a pool of requests, find the optimal feasible route.
     * If the vehicle is currently travelling along an arc, the planned route must begin with the current arc's
     * destination node.
     * If the vehicle is not currently travelling along an arc, all routes will have the current position node
     * appended to the front.
     * Feasibility is determined by the following criteria:
     * - for all requests, their pickup point is arrived at before their dropoff point,
     * - all requests are served within their time limit,
     * - at no point along the route is the vehicle's capacity exceeded,
     * - after completing the route, the vehicle will still have enough charge to reach the nearest charging station.
     *
     * @param vehicle the vehicle for which the optimal route is being calculated.
     * @param state the current decision process state.
     * @param requests the pool of requests.
     *
     * @return the route with the lowest cost.
     */
    public Route recalculate(Vehicle vehicle, DecisionProcessState state, List<Request> requests) {
        List<List<Node>> candidates = new ArrayList<>();
        recursiveAdd(candidates, new ArrayList<Node>(), requests);

        if (vehicle.isMoving()) {
            candidates.removeIf(candidate -> candidate.getFirst() != vehicle.getCurrArc().to());
        }
        else {
            candidates.forEach(candidate -> candidate.addFirst(vehicle.getCurrPos()));
        }

        List<Route> routes = new ArrayList<>();
        candidates.forEach(c -> routes.add(Route.buildFromNodeList(c)));

        routes.removeIf(route -> timeConstraintViolation(requests) || demandConstraintViolation(vehicle, route)
                || chargeConstraintViolation(state.getInstance(), vehicle, route));

        return routes.stream()
                .min(Comparator.comparing(Route::getLength))
                .orElse(null);
    }

    /**
     * Helper method for route recalculation.
     * Recursively inserts nodes into a singular candidate solution.
     * When there are no more available nodes to insert, add the completed candidate solution to a list.
     * Nodes are added pairwise on a request basis, to ensure that the dropoff node will always be after the
     * pickup node for all requests.
     * The exception to this is if a request's pickup node has already been visited, in which case, there is no
     * constraint on where the dropoff node is placed in the route order.
     *
     * @param candidates a shared list of candidate routes.
     * @param candidate a singular candidate route, represented as a list of nodes.
     * @param requests a pool of requests that gets increasingly smaller for each search.
     */
    private void recursiveAdd(List<List<Node>> candidates, List<Node> candidate, List<Request> requests) {
        if (requests.isEmpty()) {
            candidates.add(candidate);
        }
        else {
            List<Request> requestsCopy = new ArrayList<>(requests);
            Request request = requestsCopy.removeFirst();
            Node pickup = request.getPickup();
            Node dropoff = request.getDropoff();

            for (int i = 0; i < candidate.size(); i++) {
                List<Node> candidateCopy1 = new ArrayList<>(candidate);

                if (!pickup.isVisited()) {
                    candidateCopy1.add(i, pickup);

                    for (int j = i+1; j < candidateCopy1.size(); j++) {
                        List<Node> candidateCopy2 = new ArrayList<>(candidateCopy1);
                        candidateCopy2.add(j, dropoff);
                        recursiveAdd(candidates, candidateCopy2, requestsCopy);
                    }
                }
                else {
                    candidateCopy1.add(i, dropoff);
                    recursiveAdd(candidates, candidateCopy1, requestsCopy);
                }
            }
        }
    }

    /**
     * For a list of requests, check if there is time constraint violation present, i.e., the dropoff time precedes
     * the pickup time, or the time between pickup and dropoff exceeds the maximum allowed ride time.
     * Constraint violation is mainly caused by bad routing.
     *
     * @param requests the list of requests.
     *
     * @return whether violation occurred.
     */
    private boolean timeConstraintViolation(List<Request> requests) {
        return requests.stream().anyMatch(r -> r.calcRideTime() < 0 || r.calcRideTime() > r.getTMax());
    }

    /**
     * For a potential route, simulate pickup and dropoff events and check whether at any point the vehicle's demand
     * would exceed its capacity.
     *
     * @param vehicle the vehicle servicing along the route.
     * @param route the potential route.
     *
     * @return whether violation occurred.
     */
    public boolean demandConstraintViolation(Vehicle vehicle, Route route) {
        int futureDemand = vehicle.getDemand();
        List<Arc> arcs = new ArrayList<>(route.getArcs());
        arcs.addFirst(vehicle.getCurrArc());

        for (Arc arc : arcs) {
            Node to = arc.to();
            switch (to.getType()) {
                case PICKUP -> futureDemand += to.getRequest().getDemand();
                case DROPOFF -> futureDemand -= to.getRequest().getDemand();
            }

            if (futureDemand > vehicle.getCapacity()) {
                return true;
            }
        }
        return false;
    }

    /**
     * For a potential route, check whether the vehicle will have enough charge to return to a charging station
     * after completing the route.
     *
     * @param instance the problem instance.
     * @param vehicle the vehicle servicing along the route.
     * @param route the potential route.
     *
     * @return whether violation occurred.
     */
    public boolean chargeConstraintViolation(Instance instance, Vehicle vehicle, Route route) {
        Node endpoint = route.getEndpoint();
        int routeLength = route.getLength();
        int returnLength = endpoint.calcDist(instance.findClosestStation(endpoint));
        double estimatedChargeState = vehicle.estimateDepletion(routeLength + returnLength);
        return (estimatedChargeState < 0);
    }
}
