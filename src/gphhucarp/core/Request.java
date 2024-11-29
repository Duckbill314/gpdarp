package gphhucarp.core;

/**
 * In the dial-a-ride problem, customers submit requests to get a ride.
 * This class contains all the relevant details for a single request, including:
 * - the time the request was received,
 * - the pickup and dropoff destinations,
 * - the earliest and latest possible time the customer wishes to be picked up,
 * - the maximum ride time.
 *
 * @author William Huang
 */
public record Request(int id, float tRec, Position pickup, Position dropoff, float tEarly, float tLate, float tMax) {
    @Override
    public String toString() { return String.format("Request %d from %s to %s, received at time %f",
            id, pickup, dropoff, tRec); }
}
