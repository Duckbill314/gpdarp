package gpdarp.core;

import java.util.ArrayList;
import java.util.List;

/**
 * A Node represents a position on a 2D grid. It contains the following information:
 * - x and y co-ordinates,
 * - estimated time of arrival and departure,
 * - the type of node (pickup, dropoff, idle, or station),
 * - the request that the node belongs to (if it is not an idle or station node).
 *
 * @author William Huang
 */
public class Node {
    private int x;
    private int y;
    private boolean visited;
    private int arrivalTime;
    private int departureTime;
    private NodeType type;
    private Request request;

    public Node(int x, int y, boolean visited, int arrivalTime, int departureTime, NodeType type, Request request) {
        this.x = x;
        this.y = y;
        this.visited = visited;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.type = type;
        this.request = request;
    }

    // Initialisation constructor
    public Node(int x, int y) { this(x, y, false, -1, -1, NodeType.IDLE, null); }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isVisited() { return visited; }
    public int getArrivalTime() { return arrivalTime; }
    public int getDepartureTime() { return departureTime; }
    public NodeType getType() { return type; }
    public Request getRequest() { return request; }

    // Setters
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void visit() { visited = true; }
    public void setArrivalTime(int arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setDepartureTime(int departureTime) { this.departureTime = departureTime; }
    public void setType(NodeType type) { this.type = type; }
    public void setRequest(Request request) { this.request = request; }

    /**
     * Calculates the Euclidean distance to another node.
     *
     * @param o the other node.
     * @return the Euclidean distance between the two nodes, rounded up to the nearest integer.
     */
    public int calcDist(Node o) {
        return (int) Math.ceil(Math.sqrt(Math.pow((o.getX() - x), 2) + Math.pow((o.getY() - y), 2))); }

    @Override
    public String toString() {
        String type = "";
        switch (this.type) {
            case PICKUP -> type = "pickup";
            case DROPOFF -> type = "dropoff";
            case IDLE -> type = "idle";
            case STATION -> type = "station";
        }
        return String.format("%s(%d, %d)", type, x, y);
    }

    @Override
    public Node clone() {
        return new Node(x, y, visited, arrivalTime, departureTime, type, request);
    }

    /**
     * Helper method specifically for making idle nodes at updated times for a specific position.
     *
     * @param time the updated time.
     * @return the idle node for a certain location and time.
     */
    public Node idleClone(int time) {
        return new Node(x, y, true, time, -1, NodeType.IDLE, null);
    }

    /**
     * Node types are responsible for different reactive events.
     */
    public enum NodeType {
        PICKUP,
        DROPOFF,
        IDLE,
        STATION
    }

    /**
     * Utility method for creating deep clones of ArrayLists of Nodes.
     *
     * @param nodes the list of nodes to be cloned.
     * @return the cloned list.
     */
    public static <T extends Node> List<T> listClone(List<T> nodes) {
        List<T> clonedNodes = new ArrayList<>();
        for (T node : nodes) {
            clonedNodes.add((T) node.clone());
        }
        return clonedNodes;
    }
}
