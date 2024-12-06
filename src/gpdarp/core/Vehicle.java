package gpdarp.core;

import gpdarp.representation.route.Route;

import java.util.*;

/**
 * A vehicle is a defined construct with a set of important properties, including:
 * - capacity and demand,
 * - battery charging information,
 * - current position (i.e. last visited node) and current arc,
 * - requests and request service time,
 * - full route history and future planned route.
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
    private final double serveTime;
    private Node currPos;
    private Arc currArc;
    private List<Request> requests;
    private Route historicalRoute;
    private Route plannedRoute;

    public Vehicle(int id, int capacity, double chargeMax, double chargeState, double chargeFillRate,
                   double chargeDepletionRate, double serveTime, Node currPos) {
        this.id = id;
        this.capacity = capacity;
        this.demand = 0;
        this.chargeMax = chargeMax;
        this.chargeState = chargeState;
        this.chargeFillRate = chargeFillRate;
        this.chargeDepletionRate = chargeDepletionRate;
        this.serveTime = serveTime;
        this.currPos = currPos;
        this.currArc = null;
        this.requests = new ArrayList<Request>();
        this.historicalRoute = new Route();
        this.plannedRoute = new Route();
    }

    // Getters
    public int getId() { return id; }
    public int getCapacity() { return capacity; }
    public int getDemand() { return demand; }
    public double getChargeMax() { return chargeMax; }
    public double getChargeState() { return chargeState; }
    public double getChargeFillRate() { return chargeFillRate; }
    public double getChargeDepletionRate() { return chargeDepletionRate; }
    public double getServeTime() { return serveTime; }
    public Node getCurrPos() { return currPos; }
    public Arc getCurrArc() { return this.currArc; }
    public List<Request> getRequests() { return requests; }
    public Route getHistoricalRoute() { return historicalRoute; }
    public Route getPlannedRoute() { return plannedRoute; }

    // Setters
    public void setDemand(int demand) { this.demand = demand; }
    public void setChargeState(double chargeState) { this.chargeState = chargeState; }
    public void setCurrPos(Node currPos) { this.currPos = currPos; }
    public void setCurrArc(Arc currArc) { this.currArc = currArc; }
    public void setHistoricalRoute(Route historicalRoute) { this.historicalRoute = historicalRoute; }
    public void setPlannedRoute(Route plannedRoute) { this.plannedRoute = plannedRoute; }

    /**
     * Based on the unvisited nodes of a pool of requests, find the optimal route.
     * If the vehicle is currently travelling along an arc, the planned route must begin with the current arc's
     * destination node.
     * If the vehicle is not currently travelling along an arc, all routes will have the current position node
     * appended to the front.
     *
     * @param requests the pool of requests.
     * @return the route with the lowest cost.
     */
    public Route recalculate(List<Request> requests) {
        List<List<Node>> candidates = new ArrayList<>();
        recursiveAdd(candidates, new ArrayList<Node>(), requests);
        if (currArc != null) {
            candidates.removeIf(candidate -> candidate.getFirst() != currArc.to());
        }
        else {
            candidates.forEach(candidate -> candidate.addFirst(currPos));
        }

        List<Route> routes = new ArrayList<>();
        candidates.forEach(candidate -> routes.add(Route.buildFromNodeList(candidate)));
        return routes.stream()
                .min(Comparator.comparing(Route::getCost))
                .orElseThrow(NoSuchElementException::new);
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
            List<Request> requestsCopy = new ArrayList<>();
            Collections.copy(requestsCopy, requests);
            Request request = requestsCopy.removeFirst();
            Node pickup = request.pickup();
            Node dropoff = request.dropoff();

            for (int i = 0; i < candidate.size(); i++) {
                List<Node> candidateCopy1 = new ArrayList<>();
                Collections.copy(candidateCopy1, candidate);

                if (!pickup.isVisited()) {
                    candidateCopy1.add(i, pickup);

                    for (int j = i+1; j < candidateCopy1.size(); j++) {
                        List<Node> candidateCopy2 = new ArrayList<>();
                        Collections.copy(candidateCopy2, candidateCopy1);
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
     * Restore charge at a rate proportional to the elapsed time.
     * Charge cannot exceed maximum.
     *
     * @param tElapsed the time spent charging.
     */
    public void fill(double tElapsed) {
        chargeState += chargeFillRate * tElapsed;
        if (chargeState > chargeMax) {
            chargeState = chargeMax;
        }
    }

    /**
     * Deplete charge at a rate proportional to the elapsed time.
     * Charge should not fall below empty - however, this function allows it for the sake of determining feasibility.
     *
     * @param tElapsed the time spent charging.
     */
    public double deplete(double tElapsed) {
        chargeState -= chargeDepletionRate * tElapsed;
        return chargeState;
    }

    @Override
    public String toString() { return String.format("Vehicle %d | charge: %f/%f", id, chargeState, chargeMax); }
}
