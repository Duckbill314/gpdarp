package gpdarp.decisionprocess.tiebreaker;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.TieBreaker;
import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * A random tiebreaker that uniformly selects one of the two options.
 *
 * @author gphhucarp, William Huang
 */
public class RandomTieBreaker extends TieBreaker {
    private final RandomDataGenerator rdg;

    public RandomTieBreaker(RandomDataGenerator rdg) {
        this.rdg = rdg;
    }

    @Override
    public int breakTie(Vehicle v1, Vehicle v2) {
        double r = rdg.nextUniform(0, 1);
        return (r < 0.5) ? -1 : 1;
    }

    @Override
    public int breakTie(Request r1, Request r2) {
        double r = rdg.nextUniform(0, 1);
        return (r < 0.5) ? -1 : 1;
    }
}
