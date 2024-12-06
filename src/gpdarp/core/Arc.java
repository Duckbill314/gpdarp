package gpdarp.core;

/**
 * An arc is a directed edge between two nodes. It has:
 *  - (from, to) nodes,
 *  - serving cost (proportional to length).
 *
 * @author William Huang
 */

public record Arc(Node from, Node to, double serveCost) implements Comparable<Arc> {
    public Arc(Node from, Node to) {
        this(from, to, from.calcDist(to));
    }

    @Override
    public String toString() {
        return String.format("(%s, %s): sc = %f", from, to, serveCost);
    }

    @Override
    public int compareTo(Arc o) {
        return Double.compare(serveCost, o.serveCost());
    }
}
