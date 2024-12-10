package gpdarp.decisionprocess.tiebreaker;

import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.TieBreaker;

/**
 * A simple tiebreaker between two vehicles uses the natural comparator.
 *
 * @author gphhucarp, William Huang
 */

public class SimpleTieBreaker extends TieBreaker {
    @Override
    public int breakTie(Vehicle v1, Vehicle v2) {
        return v1.compareTo(v2);
    }
}
