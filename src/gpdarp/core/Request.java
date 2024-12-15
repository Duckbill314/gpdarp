package gpdarp.core;

import java.util.ArrayList;
import java.util.List;

/**
 * In the dial-a-ride problem, customers submit getRequests to get a ride.
 * This class contains all the relevant details for a single request, including:
 * - the time the request was received,
 * - the pickup and dropoff destinations,
 * - the earliest and latest possible time the customer wishes to be picked up,
 * - the maximum ride time.
 * In addition, it also has a pseudo-flag for checking whether the request has been fulfilled,
 * and a temporary priority value for the purpose of vehicle allocation.
 *
 * @author William Huang
 */
public final class Request {
    private final int id;
    private final int tRec;
    private final Node pickup;
    private final Node dropoff;
    private final int tEarly;
    private final int tLate;
    private final int tMax;
    private Vehicle vehicle;
    private double priority;

    public Request(int id, int tRec, Node pickup, Node dropoff, int tEarly, int tLate, int tMax) {
        this.id = id;
        this.tRec = tRec;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.tEarly = tEarly;
        this.tLate = tLate;
        this.tMax = tMax;
        this.vehicle = null;
        pickup.setRequest(this);
        pickup.setType(Node.NodeType.PICKUP);
        dropoff.setRequest(this);
        dropoff.setType(Node.NodeType.DROPOFF);
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
    public Vehicle getVehicle() { return vehicle; }
    public double getPriority() { return priority; }

    // Setters
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public void setPriority(double priority) { this.priority = priority; }

    /**
     * A request is fulfilled if both its pickup and dropoff nodes have been visited.
     *
     * @return the fulfillment status.
     */
    public boolean isFulfilled() {
        return pickup.isVisited() && dropoff.isVisited();
    }

    /**
     * Calculated the estimated or actual ride time based on the times the pickup and dropoff points are visited.
     *
     * @return the ride time.
     */
    public int calcRideTime() {
        if (pickup.getEta() == -1 || dropoff.getEta() == -1) {
            return -1;
        }
        return dropoff.getEta() - pickup.getEta();
    }

    /**
     * Once a request has been fulfilled, it should remove itself from its associated vehicle's list of requests.
     */
    public void finalise() { vehicle.getRequests().remove(this); }

    /**
     * Natural comparator that prefers the request with the received time.
     *
     * @param o the other request to which this request is being compared against.
     *
     * @return this request's priority value.
     */
    public int compareTo(Request o) { return tRec - o.getTRec(); }

    @Override
    public String toString() {
        return String.format("Request %d from %s to %s, received at time %f, %s",
                id, pickup, dropoff, tRec, (isFulfilled()) ? "fulfilled" : "not fulfilled");
    }

    @Override
    public Request clone() {
        return new Request(id, tRec, pickup.clone(), dropoff.clone(), tEarly, tLate, tMax);
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
