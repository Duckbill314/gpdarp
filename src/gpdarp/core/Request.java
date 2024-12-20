package gpdarp.core;

import java.util.ArrayList;
import java.util.List;

/**
 * In the dial-a-ride problem, customers submit Requests to get a ride.
 * This class contains all the relevant details for a single request, including:
 * - the time the request was received,
 * - the pickup and dropoff destinations,
 * - the earliest and latest possible time the customer wishes to be picked up,
 * - the maximum ride time.
 * In addition, it also has a type identifier and a temporary priority value for the purpose of vehicle allocation.
 *
 * @author William Huang
 */
public class Request {
    private final int id;
    private final int tRec;
    private final Node pickup;
    private final Node dropoff;
    private final int tEarly;
    private final int tLate;
    private final int tMax;
    private final int demand;
    private RequestType type;
    private double priority;

    public Request(int id, int tRec, Node pickup, Node dropoff, int tEarly, int tLate, int tMax, int demand) {
        this.id = id;
        this.tRec = tRec;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.tEarly = tEarly;
        this.tLate = tLate;
        this.tMax = tMax;
        this.demand = demand;
        this.type = RequestType.REQUEST;
        this.priority = 0;
        pickup.setRequest(this);
        pickup.setType(Node.NodeType.PICKUP);
        dropoff.setRequest(this);
        dropoff.setType(Node.NodeType.DROPOFF);
    }

    public Request(int time, Node pos, Node station) {
        this(-1, time, pos, station, 0, (int) Double.POSITIVE_INFINITY, (int) Double.POSITIVE_INFINITY, 0);
        this.type = RequestType.CHARGE;
    }

    // Getters
    public int getId() { return id; }
    public int getTRec() { return tRec; }
    public Node getPickup() { return pickup; }
    public Node getDropoff() { return dropoff; }
    public int getTEarly() { return tEarly; }
    public int getTLate() { return tLate; }
    public int getTMax() { return tMax; }
    public int getDemand() { return demand; }
    public RequestType getType() { return type; }
    public double getPriority() { return priority; }

    // Setters
    public void setType(RequestType type) { this.type = type; }
    public void setPriority(double priority) { this.priority = priority; }

    /**
     * Request types are responsible for handling behaviour during reactive events.
     */
    public enum RequestType {
        REQUEST,
        CHARGE
    }

    /**
     * Compare the request to another request on the basis of their id number.
     * This is a quite meaningless natural comparator, because other more meaningful comparisons are made explicitly
     * whenever necessary.
     *
     * @param o the other request.
     * @return priority of this request in comparison to the other request.
     */
    public int compareTo(Request o) { return id - o.getId(); }

    @Override
    public String toString() {
        return String.format("Request %d from %s to %s, received at time %d", id, pickup, dropoff, tRec);
    }

    @Override
    public Request clone() {
        Request clone = new Request(id, tRec, pickup.clone(), dropoff.clone(), tEarly, tLate, tMax, demand);
        clone.setPriority(priority);
        clone.setType(type);
        return clone;
    }

    /**
     * Utility method for creating deep clones of ArrayLists of Requests.
     *
     * @param requests the list of requests to be cloned.
     * @return the cloned list.
     */
    public static <T extends Request> List<T> listClone(List<T> requests) {
        List<T> clonedRequests = new ArrayList<>();
        for (Request request : requests) {
            clonedRequests.add((T) request.clone());
        }
        return clonedRequests;
    }
}
