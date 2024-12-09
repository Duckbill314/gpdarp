package gpdarp.decisionprocess;

import gpdarp.core.Vehicle;

/**
 * A tiebreaker breaks the tie between two vehicle allocations when they have the same priority.
 *
 * @author gphhucarp, William Huang
 */

public abstract class TieBreaker {
    public abstract int breakTie(Vehicle v1, Vehicle v2);
}
