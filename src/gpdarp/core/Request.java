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
 * In addition, it also has a pseudo-flag for checking whether the request has been fulfilled.
 *
 * @author William Huang
 */
public record Request(int id, float tRec, Node pickup, Node dropoff, float tEarly, float tLate, float tMax) {

    /**
     * A request is fulfilled if both its pickup and dropoff nodes have been visited.
     *
     * @return the fulfillment status.
     */
    public boolean isFulfilled() { return pickup.isVisited() && dropoff.isVisited(); }

    @Override
    public String toString() { return String.format("Request %d from %s to %s, received at time %f, %s",
            id, pickup, dropoff, tRec, (isFulfilled()) ? "fulfilled" : "not fulfilled"); }

    @Override
    public Request clone() { return new Request(id, tRec, pickup.clone(), dropoff.clone(), tEarly, tLate, tMax); }

    /**
     * Utility method for creating deep clones of ArrayLists of Requests.
     *
     * @param requests the list of requests to be cloned.
     *
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
