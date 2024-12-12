package gpdarp.core;

/**
 * A Node represents a position on a 2D grid. It contains the following information:
 * - x and y co-ordinates,
 * - whether it has been visited,
 * - estimated time of arrival (this value can be assigned dynamically during route planning but should remain fixed
 * once the node has been visited),
 * - the type of node (pickup, dropoff, or station),
 * - the request that the node belongs to (if it is not a station node).
 *
 * @author William Huang
 */
public class Node {
    private int x;
    private int y;
    private boolean visited;
    private int eta;
    private NodeType type;
    private Request request;

    public Node(int x, int y, boolean visited, int eta, NodeType type, Request request) {
        this.x = x;
        this.y = y;
        this.visited = visited;
        this.eta = eta;
        this.type = type;
        this.request = request;
    }

    // Initialisation constructor
    public Node(int x, int y) { this(x, y, false, -1, null, null); }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isVisited() { return visited; }
    public int getEta() { return eta; }
    public NodeType getType() { return type; }
    public Request getRequest() { return request; }

    // Setters
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void visit() { visited = true; }
    public void setEta(int eta) { this.eta = eta; }
    public void setType(NodeType type) { this.type = type; }
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
    public Node clone() { return new Node(x, y, visited, eta, type, request.clone()); }

    /**
     * Node types are responsible for different reactive events.
     */
    public enum NodeType {
        PICKUP,
        DROPOFF,
        STATION;
    }
}
