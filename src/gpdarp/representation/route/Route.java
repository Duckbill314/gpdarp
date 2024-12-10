package gpdarp.representation.route;

import gpdarp.core.Arc;
import gpdarp.core.Node;

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
    public Node getEndpoint() { return arcs.getLast().to(); }

    // Setters
    public void setArcs(List<Arc> arcs) { this.arcs = arcs; }

    // Manipulators
    public void push(Arc arc) { arcs.add(arc); }
    public Arc pop() { return arcs.removeFirst(); }

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
     * Calculate the sum of the lengths of all arcs in the route.
     *
     * @return the total length.
     */
    public int getLength() {
        if (arcs.isEmpty()) {
            return 0;
        }
        int length = 0;
        for (Arc arc : arcs) {
            length += arc.length();
        }
        return length;
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
