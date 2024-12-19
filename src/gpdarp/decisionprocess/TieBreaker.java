package gpdarp.decisionprocess;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;

/**
 * A tiebreaker breaks the tie between two allocations when they have the same priority.
 *
 * @author gphhucarp, William Huang
 */

public abstract class TieBreaker {
    /**
     * Break the tie between two vehicles by returning the priority of one vehicle against another.
     *
     * @param v1 vehicle 1.
     * @param v2 vehicle 2.
     * @return the priority of v1 against v2.
     */
    public abstract int breakTie(Vehicle v1, Vehicle v2);

    /**
     * Break the tie between two requests by returning the priority of one request against another.
     *
     * @param r1 request 1.
     * @param r2 request 2.
     * @return the priority of r1 against r2.
     */
    public abstract int breakTie(Request r1, Request r2);
}
