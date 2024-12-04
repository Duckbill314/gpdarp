package gpdarp.core;

/**
 * Position on a 2D grid.
 * This class is a lightweight equivalent to what would typically be a Node class.
 * Because (most) positions in this problem aren't reused in any way, there is little need for a Node class.
 *
 * @author William Huang
 */
public class Position {
    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }

    // Setters
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    // Manipulators
    public void addX(double x) { this.x += x; }
    public void addY(double y) { this.y += y; }

    /**
     * Calculates the Euclidean distance to another node.
     *
     * @param o the other node.
     * @return the Euclidean distance between the two nodes.
     */
    public double calcDist(Position o) { return Math.sqrt(Math.pow((o.getX() - x), 2) + Math.pow((o.getY() - y), 2)); }

    @Override
    public String toString() { return String.format("(%f, %f)", x, y); }

    public boolean equals(Position o) { return ((this.x == o.getX()) && (this.y == o.getY())); }
}
