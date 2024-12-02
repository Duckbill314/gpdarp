package gphhucarp.core;

/**
 * An arc is a directed edge between two positions. It has:
 *  - (from, to) positions,
 *  - serving cost (proportional to length).
 *
 * @author William Huang
 */

public record Arc(Position from, Position to, double serveCost) implements Comparable<Arc> {
    public Arc(Position from, Position to) {
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
