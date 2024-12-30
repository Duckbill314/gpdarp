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
    private Vehicle vehicle;
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
        this.vehicle = null;
        this.type = RequestType.REQUEST;
        this.priority = 0;
        pickup.setRequest(this);
        pickup.setType(Node.NodeType.PICKUP);
        dropoff.setRequest(this);
        dropoff.setType(Node.NodeType.DROPOFF);
    }

    // Constructor for charging requests
    public Request(int time, Node pos, Node station) {
        this.id = -1;
        this.tRec = time;
        this.pickup = pos;
        this.dropoff = station;
        this.tEarly = 0;
        this.tLate = (int) Double.POSITIVE_INFINITY;
        this.tMax = (int) Double.POSITIVE_INFINITY;
        this.demand = 0;
        this.vehicle = null;
        this.type = RequestType.CHARGE;
        this.priority = 0;
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
    public Vehicle getVehicle() { return vehicle; }
    public RequestType getType() { return type; }
    public double getPriority() { return priority; }

    // Setters
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
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
     * Calculated the estimated or actual ride time based on the times the pickup and dropoff points are visited.
     *
     * @return the ride time.
     */
    public int calcRideTime() {
        if (pickup.getDepartureTime() == -1 || dropoff.getArrivalTime() == -1) {
            return -1;
        }
        return dropoff.getArrivalTime() - pickup.getDepartureTime();
    }

    /**
     * Once a request has been fulfilled, it should remove itself from its associated vehicle's list of requests.
     */
    public void finalise() { vehicle.getRequests().remove(this); }

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
    public static List<Request> listClone(List<Request> requests) {
        List<Request> clonedRequests = new ArrayList<>();
        for (Request request : requests) {
            clonedRequests.add(request.clone());
        }
        return clonedRequests;
    }
}
