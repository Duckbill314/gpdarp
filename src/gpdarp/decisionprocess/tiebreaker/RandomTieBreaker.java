package gpdarp.decisionprocess.tiebreaker;

import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.TieBreaker;
import org.apache.commons.math3.random.RandomDataGenerator;

public class RandomTieBreaker extends TieBreaker {

    private RandomDataGenerator rdg;

    public RandomTieBreaker(RandomDataGenerator rdg) {
        this.rdg = rdg;
    }

    @Override
    public int breakTie(Vehicle v1, Vehicle v2) {
        double r = rdg.nextUniform(0, 1);

        if (r < 0.5)
            return -1;

        return 1;
    }
}
