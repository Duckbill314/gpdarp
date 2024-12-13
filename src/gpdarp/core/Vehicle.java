package gpdarp.core;

import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.representation.route.Route;

import java.util.*;

/**
 * A vehicle is a defined construct with a set of important properties, including:
 * - capacity and demand,
 * - battery charging information,
 * - the latest known idle position (implies that requests are not being served),
 * - the current arc (implies that requests are being served),
 * - requests that have been allocated to the vehicle,
 * - full route history and future planned route.
 * It also has a "temporary" priority value for the purpose of request allocation.
 *
 * @author William Huang
 */
public class Vehicle {
    private final int id;
    private final int capacity;
    private int demand;
    private final double chargeMax;
    private double chargeState;
    private final double chargeFillRate;
    private final double chargeDepletionRate;
    private final int serveTime;
    private Node currPos;
    private Arc currArc;
    private List<Request> requests;
    private Route historicalRoute;
    private Route plannedRoute;
    private double priority;

    public Vehicle(int id, int capacity, int demand, double chargeMax, double chargeState, double chargeFillRate,
                   double chargeDepletionRate, int serveTime, Node currPos, Arc currArc, List<Request> requests,
                   Route historicalRoute, Route plannedRoute, double priority) {
        this.id = id;
        this.capacity = capacity;
        this.demand = demand;
        this.chargeMax = chargeMax;
        this.chargeState = chargeState;
        this.chargeFillRate = chargeFillRate;
        this.chargeDepletionRate = chargeDepletionRate;
        this.serveTime = serveTime;
        this.currPos = currPos;
        this.currArc = currArc;
        this.requests = requests;
        this.historicalRoute = historicalRoute;
        this.plannedRoute = plannedRoute;
        this.priority = priority;
    }

    // Initialisation constructor
    public Vehicle(int id, int capacity, double chargeMax, double chargeState, double chargeFillRate,
                   double chargeDepletionRate, int serveTime, Node currPos) {
        this(id, capacity, 0, chargeMax, chargeState, chargeFillRate, chargeDepletionRate, serveTime,
                currPos, null, new ArrayList<Request>(), new Route(), new Route(), 0.0);
    }

    // Getters
    public int getId() { return id; }
    public int getCapacity() { return capacity; }
    public int getDemand() { return demand; }
    public double getChargeMax() { return chargeMax; }
    public double getChargeState() { return chargeState; }
    public double getChargeFillRate() { return chargeFillRate; }
    public double getChargeDepletionRate() { return chargeDepletionRate; }
    public int getServeTime() { return serveTime; }
    public Node getCurrPos() { return currPos; }
    public Arc getCurrArc() { return this.currArc; }
    public List<Request> getRequests() { return requests; }
    public Route getHistoricalRoute() { return historicalRoute; }
    public Route getPlannedRoute() { return plannedRoute; }
    public double getPriority() { return priority; }
    public int getRemainingCapacity() { return capacity - demand; }
    public boolean isMoving() { return (currArc != null); }

    // Setters
    public void setDemand(int demand) { this.demand = demand; }
    public void setChargeState(double chargeState) { this.chargeState = chargeState; }
    public void setCurrPos(Node currPos) { this.currPos = currPos; }
    public void setCurrArc(Arc currArc) { this.currArc = currArc; }
    public void setRequests(List<Request> requests) { this.requests = requests; }
    public void setHistoricalRoute(Route historicalRoute) { this.historicalRoute = historicalRoute; }
    public void setPlannedRoute(Route plannedRoute) { this.plannedRoute = plannedRoute; }
    public void setPriority(double priority) { this.priority = priority; }

    /**
     * Based on the unvisited nodes of a pool of requests, find the optimal route.
     * If the vehicle is currently travelling along an arc, the planned route must begin with the current arc's
     * destination node.
     * If the vehicle is not currently travelling along an arc, all routes will have the current position node
     * appended to the front.
     * The route must satisfy the constraint that all requests are served within the time limit and that for all
     * requests, their pickup point is arrived at before their dropoff point.
     *
     * @param requests the pool of requests.
     *
     * @return the route with the lowest cost.
     */
    public Route recalculate(DecisionProcessState state, List<Request> requests) {
        List<List<Node>> candidates = new ArrayList<>();
        recursiveAdd(candidates, new ArrayList<Node>(), requests);
        if (isMoving()) {
            candidates.removeIf(candidate -> candidate.getFirst() != currArc.to());
        }
        else {
            candidates.forEach(candidate -> candidate.addFirst(currPos));
        }

        List<Route> routes = new ArrayList<>();
        candidates.forEach(c -> routes.add(Route.buildFromNodeList(c)));

        for (Route route : routes) {
            route.updateEtas(state, this);
            if (Request.timeConstraintViolation(requests)) {
                routes.remove(route);
            }
        }

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
     * Convenient method for ensuring the relationship between a request and its assigned vehicle is
     * established properly.
     *
     * @param request the request to allocate to this vehicle.
     */
    public void allocate(Request request) {
        request.setVehicle(this);
        requests.add(request);
    }

    /**
     * Convenience method for ensuring that the vehicle's route and the ETA of nodes along the route are
     * properly updated.
     *
     * @param state the current state.
     * @param plannedRoute the optimal route.
     */
    public void updateRoute(DecisionProcessState state, Route plannedRoute) {
        plannedRoute.updateEtas(state, this);
        setPlannedRoute(plannedRoute);
    }

    /**
     * Convenience method for handling pickup events.
     *
     * @param node the node at which the event occurs.
     */
    public void pickup(Node node) {
        node.visit();
        demand++;
        deplete(currArc.length());
        historicalRoute.push(currArc);
    }

    /**
     * Convenience method for handling dropoff events.
     *
     * @param node the node at which the event occurs.
     */
    public void dropoff(Node node) {
        node.visit();
        node.getRequest().finalise();
        demand--;
        deplete(currArc.length());
        historicalRoute.push(currArc);
    }

    /**
     * Helper method that updates a vehicle's current arc with the next arc in its planned route.
     *
     * @return the next destination node (for the purpose of invoking a new event).
     */
    public Node updateArcFromPlannedRoute() {
        Arc currArc = getPlannedRoute().pop();
        setCurrArc(currArc);
        if (currArc == null) {
            return null;
        }
        return currArc.to();
    }

    /**
     * Restore charge at a rate proportional to the elapsed time.
     * Charge cannot exceed maximum.
     *
     * @param tElapsed the time spent charging.
     */
    public void fill(int tElapsed) {
        chargeState += chargeFillRate * tElapsed;
        if (chargeState > chargeMax) {
            chargeState = chargeMax;
        }
    }

    /**
     * Estimates the time required to fully charge.
     *
     * @return the estimated charge time.
     */
    public int estimateFillTime() {
        double missing = chargeMax - chargeState;
        return (int) Math.ceil(missing / chargeFillRate);
    }

    /**
     * Deplete charge at a rate proportional to a travelled length.
     * Charge should not fall below empty, but the safeguard in place is only there for potential rounding errors.
     * For proper functionality, depletion actions should always be checked for feasibility before they are taken.
     *
     * @param length the length travelled.
     */
    public void deplete(int length) {
        chargeState -= chargeDepletionRate * length;
        if (chargeState < 0) {
            chargeState = 0;
        }
    }

    /**
     * Estimation of resulting charge from travelling a given length.
     * Always check using this method before actually depleting!
     *
     * @param length the length travelled.
     *
     * @return the resulting charge state.
     */
    public double estimateDepletion(int length) { return chargeState - chargeDepletionRate * length; }

    @Override
    public String toString() { return String.format("Vehicle %d | charge: %f/%f", id, chargeState, chargeMax); }

    @Override
    public Vehicle clone() {
        return new Vehicle(id, capacity, demand, chargeMax, chargeState, chargeFillRate, chargeDepletionRate,
                serveTime, currPos.clone(), currArc.clone(), Request.listClone(requests),
                historicalRoute.clone(), plannedRoute.clone(), priority);
    }

    /**
     * Utility method for creating deep clones of ArrayLists of Vehicles.
     *
     * @param vehicles the list of vehicles to be cloned.
     *
     * @return the cloned list.
     */
    public static List<Vehicle> listClone(List<Vehicle> vehicles) {
        List<Vehicle> clonedVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            clonedVehicles.add(vehicle.clone());
        }
        return clonedVehicles;
    }

    /**
     * Compare the vehicle to another vehicle on the basis of their id number.
     * This is a quite meaningless natural comparator, because other more meaningful comparisons are made explicitly
     * whenever necessary.
     *
     * @param o the other vehicle.
     * @return priority of this vehicle in comparison to the other vehicle.
     */
    public int compareTo(Vehicle o) { return (id - o.getId()); }
}
