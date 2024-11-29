package gphhucarp.core;

/**
 * An arc is a directed edge between two positions. It has:
 *  - (from, to) positions,
 *  - serving cost (proportional to length).
 *
 * @author William Huang
 */

public class Arc implements Comparable<Arc> {
    private Position from;
    private Position to;
    private final double serveCost;

    public Arc(Position from, Position to) {
        this.from = from;
        this.to = to;
        serveCost = from.calcDist(to);
    }

    // Getters
    public Position getFrom() {
        return from;
    }
    public Position getTo() {
        return to;
    }
    public double getServeCost() {
        return serveCost;
    }

    // Setters
    public void setFrom(Position from) { this.from = from; }
    public void setTo(Position to) { this.to = to; }

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
