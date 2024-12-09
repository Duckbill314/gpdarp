package gpdarp.decisionprocess.tiebreaker;

import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.TieBreaker;

/**
 * A simple tie breaker between two arcs uses the natural comparator.
 */

public class SimpleTieBreaker extends TieBreaker {

    @Override
    public int breakTie(Vehicle v1, Vehicle v2) {
        return v1.compareTo(v2);
    }
}
