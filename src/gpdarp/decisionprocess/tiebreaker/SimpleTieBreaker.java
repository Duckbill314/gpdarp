package gpdarp.decisionprocess.tiebreaker;

import gpdarp.core.Arc;
import gpdarp.decisionprocess.TieBreaker;

/**
 * A simple tie breaker between two arcs uses the natural comparator.
 */

public class SimpleTieBreaker extends TieBreaker {

    @Override
    public int breakTie(Arc arc1, Arc arc2) {
        return arc1.compareTo(arc2);
    }
}
