package gpdarp.core;

/**
 * In the dial-a-ride problem, customers submit requests to get a ride.
 * This class contains all the relevant details for a single request, including:
 * - the time the request was received,
 * - the pickup and dropoff destinations,
 * - the earliest and latest possible time the customer wishes to be picked up,
 * - the maximum ride time.
 * In addition, it also has a flag for checking whether the request has been fulfilled.
 *
 * @author William Huang
 */
public record Request(int id, float tRec, Position pickup, Position dropoff, float tEarly, float tLate, float tMax,
                      boolean fulfilled) {

    public Request(int id, float tRec, Position pickup, Position dropoff, float tEarly, float tLate, float tMax) {
        this(id, tRec, pickup, dropoff, tEarly, tLate, tMax, false);
    }
    @Override
    public String toString() { return String.format("Request %d from %s to %s, received at time %f, %s",
            id, pickup, dropoff, tRec, (fulfilled) ? "fulfilled" : "not fulfilled"); }
}
