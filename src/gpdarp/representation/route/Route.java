package gpdarp.representation.route;

import gpdarp.core.*;
import gpdarp.decisionprocess.DecisionProcessState;

import java.util.ArrayList;
import java.util.List;

/**
 * A route is ostensibly a collection of arcs - nothing more, nothing less.
 * The main information it maintains is the collection of arcs, and implicitly, the cost of the route.
 * Other information such as position, capacity and demand are handled directly by the Vehicle class.
 *
 * @author William Huang
 */
public class Route {
    private List<Arc> arcs;

    public Route(List<Arc> arcs) { this.arcs = arcs; }

    // Initialisation constructor
    public Route() { this(new ArrayList<Arc>()); }

    // Getters
    public List<Arc> getArcs() { return arcs; }
    public Node getStartpoint() { return arcs.getFirst().from(); }
    public Node getEndpoint() { return arcs.getLast().to(); }

    // Setters
    public void setArcs(List<Arc> arcs) { this.arcs = arcs; }

    // Manipulators
    public void push(Arc arc) { arcs.add(arc); }
    public Arc pop() {
        if (arcs.isEmpty()) {
            return null;
        }
        return arcs.removeFirst();
    }

    /**
     * Build a route from a list of nodes by converting the node list to an arc list.
     * Used for dynamic route recalculation.
     *
     * @param nodeList the list of nodes.
     * @return the corresponding route.
     */
    public static Route buildFromNodeList(List<Node> nodeList) {
        List<Arc> arcList = new ArrayList<>();
        for (int i = 0; i < nodeList.size()-1; i++) {
            arcList.add(new Arc(nodeList.get(i), nodeList.get(i+1)));
        }
        return new Route(arcList);
    }

    /**
     * Updates the estimated arrival times for all nodes in the route.
     * If the vehicle is moving:
     * - the route is set to begin from the current arc's destination,
     * - at that node's pre-established estimated arrival time,
     * - with each arc being deterministic from the one before it,
     * - and including serve costs explicitly.
     * If the vehicle is stationary:
     * - the route is set to begin from the current position,
     * - at either the current state's time or the vehicle's next available time (whichever comes later),
     * - and there will be an additional arc present to begin the chain,
     * - that includes the serve cost implicitly, i.e., pending availability.
     *
     * @param state the state of the decision process.
     * @param vehicle the vehicle associated with the route.
     */
    public void updateEtas(DecisionProcessState state, Vehicle vehicle) {
        Instance instance = state.getInstance();

        Arc arc;
        int startTime;
        int serveTime;
        int i = 0;

        if (!vehicle.isMoving()) {
            arc = arcs.get(i);
            startTime = Math.max(state.getTime(), vehicle.getCurrPos().getEta());
            arc.to().setEta(startTime + instance.calculateTravelTime(arc.length()));
            i++;
        }

        while (i < arcs.size()) {
            arc = arcs.get(i);
            startTime = arc.from().getEta();
            serveTime = vehicle.getServeTime();
            arc.to().setEta(startTime + serveTime + instance.calculateTravelTime(arc.length()));
            i++;
        }
    }

    /**
     * Calculate the sum of the lengths of all arcs in the route.
     *
     * @return the total length.
     */
    public int getLength() {
        return arcs.stream()
                .map(Arc::length)
                .reduce(0, Integer::sum);
    }

    /**
     * Resets the route by replacing the arc list with an empty list.
     */
    public void reset() { arcs = new ArrayList<Arc>(); }

    @Override
    public Route clone() { return new Route(Arc.listClone(arcs)); }

    /**
     * Utility method for creating deep clones of ArrayLists of Routes.
     *
     * @param routes the list of routes to be cloned.
     *
     * @return the cloned list.
     */
    public static List<Route> listClone(List<Route> routes) {
        ArrayList<Route> clonedRoutes = new ArrayList<>();
        for (Route route : routes) {
            clonedRoutes.add(route.clone());
        }
        return clonedRoutes;
    }

    public boolean isEmpty() { return arcs.isEmpty(); }

    @Override
    public String toString() {
        if (arcs.isEmpty()) {
            return "Empty route";
        }
        StringBuilder str = new StringBuilder();
        str.append(arcs.getFirst().from());
        for (Arc arc : arcs) {
            str.append(arc.to());
        }
        return str.toString();
    }
}
