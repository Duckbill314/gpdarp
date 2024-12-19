package gpdarp.representation.route;

import gpdarp.core.*;

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

    /**
     * The penalty is the sum of all the time that a vehicle was late to pick up a request.
     *
     * @return the penalty.
     */
    public int calculatePenalty() {
        return arcs.stream()
                .map(Arc::from)
                .filter(n -> n.getType() == Node.NodeType.PICKUP)
                .map(n -> n.getTime() - n.getRequest().getTLate())
                .filter(t -> t > 0)
                .reduce(0, Integer::sum);
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
     * @return the cloned list.
     */
    public static List<Route> listClone(List<Route> routes) {
        ArrayList<Route> clonedRoutes = new ArrayList<>();
        for (Route route : routes) {
            clonedRoutes.add(route.clone());
        }
        return clonedRoutes;
    }

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
