package gpdarp.core;

/**
 * A Node represents a position on a 2D grid.
 *
 * @author William Huang
 */
public class Node {
    private int x;
    private int y;
    private boolean visited;
    private int eta;
    private Request request = null;

    // TODO: node types

    public Node(int x, int y, boolean visited, int eta) {
        this.x = x;
        this.y = y;
        this.visited = visited;
        this.eta = eta;
    }

    // Initialisation constructor
    public Node(int x, int y) { this(x, y, false, -1); }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isVisited() { return visited; }
    public int getEta() { return eta; }
    public Request getRequest() { return request; }

    // Setters
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void visit() { visited = true; }
    public void setEta(int eta) { this.eta = eta; }
    public void setRequest(Request request) { this.request = request; }

    /**
     * Calculates the Euclidean length to another node.
     *
     * @param o the other node.
     * @return the Euclidean length between the two nodes.
     */
    public int calcDist(Node o) {
        return (int) Math.ceil(Math.sqrt(Math.pow((o.getX() - x), 2) + Math.pow((o.getY() - y), 2))); }

    @Override
    public String toString() { return String.format("(%d, %d)", x, y); }

    @Override
    public Node clone() { return new Node(x, y, visited, eta); }

    public enum NodeType {
        PICKUP,
        DROPOFF,
        STATION;
    }
}
