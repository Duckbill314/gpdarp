package gpdarp.core;

/**
 * A Node represents a position on a 2D grid.
 * This class is lightweight in comparison to what is typical for graph problems.
 *
 * @author William Huang
 */
public class Node {
    private double x;
    private double y;
    private boolean visited;

    public Node(double x, double y, boolean visited) {
        this.x = x;
        this.y = y;
        this.visited = visited;
    }

    // Initialisation constructor
    public Node(double x, double y) { this(x, y, false); }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isVisited() { return visited; }

    // Setters
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void visit() { visited = true; }

    /**
     * Calculates the Euclidean distance to another node.
     *
     * @param o the other node.
     * @return the Euclidean distance between the two nodes.
     */
    public double calcDist(Node o) { return Math.sqrt(Math.pow((o.getX() - x), 2) + Math.pow((o.getY() - y), 2)); }

    @Override
    public String toString() { return String.format("(%f, %f)", x, y); }

    @Override
    public Node clone() { return new Node(x, y, visited); }
}
