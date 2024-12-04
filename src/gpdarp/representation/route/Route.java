package gpdarp.representation.route;

import gpdarp.core.Arc;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class of a route.
 * A route is ostensibly a collection of arcs - nothing more, nothing less.
 * The main information it maintains is the collection of arcs, and implicitly, the cost of the route.
 * Other information such as position, capacity and demand are handled directly by the Vehicle class.
 *
 * @author William Huang
 */
public class Route {
    private List<Arc> arcs;

    public Route(List<Arc> arcs) { this.arcs = arcs; }

    public Route() { this(new ArrayList<Arc>()); }

    // Getters
    public List<Arc> getArcs() { return arcs; }

    // Setters
    public void setArcs(List<Arc> arcs) { this.arcs = arcs; }

    // Manipulators
    public void push(Arc arc) { arcs.add(arc); }
    public Arc pop() { return arcs.removeFirst(); }

    /**
     * Calculate the cost of the route as the sum of the serving costs of all arcs in the route.
     *
     * @return the total cost.
     */
    public double getCost() {
        if (arcs.isEmpty()) {
            return 0;
        }
        double cost = 0;
        for (Arc arc : arcs) {
            cost += arc.serveCost();
        }
        return cost;
    }

    public void reset() { arcs = new ArrayList<Arc>(); }

    public Route clone() { return new Route(arcs); }

    public String toString() {
        if (arcs.isEmpty()) {
            return "Empty route";
        }
        String str = "";
        str += arcs.getFirst().from();
        for (Arc arc : arcs) {
            str += arc.to();
        }
        return str;
    }
}
