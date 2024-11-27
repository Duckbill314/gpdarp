package gphhucarp.core;

/**
 * An arc is a directed edge of the graph.
 *
 * It has:
 *  - (from, to) nodes,
 *  - serving cost (proportional to length)
 *
 * Natural comparison: a < b if a has smaller cost than b
 *
 * In addition, it has a priority field for decision-making process
 *
 * @author gphhucarp, William Huang
 */

public class Arc implements Comparable<Arc> {
    private Node from;
    private Node to;
    private double serveCost;
    private double priority;

    public Arc(Node from, Node to) {
        this.from = from;
        this.to = to;
        serveCost = from.calcDist(to);
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public double getServeCost() {
        return serveCost;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    /**
     * Whether this arc is prior to another arc.
     * An arc is prior to another arc if
     *   (1) it has a smaller priority value, or
     *   (2) they have the same priority, and this arc is better than the other arc.
     * @param o the other arc.
     * @return true if this arc is prior to the other, and false otherwise.
     */
    public boolean priorTo(Arc o) {
        if (Double.compare(priority, o.priority) < 0)
            return true;

        if (Double.compare(priority, o.priority) > 0)
            return false;

        return compareTo(o) < 0;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s): sc = %f", from, to, serveCost);
    }

    public String toSimpleString() {
        return String.format("(%s, %s)", from, to);
    }

    @Override
    public int compareTo(Arc o) {
        return Double.compare(serveCost, o.getServeCost());
    }
}
