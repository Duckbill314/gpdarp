package gpdarp.decisionprocess;

import gpdarp.core.*;
import gpdarp.representation.Solution;
import gpdarp.representation.route.Route;

import java.util.*;

/**
 * TODO
 *
 * @author gphhucarp, William Huang
 */
public class DecisionProcessState {

    private Instance instance;
    private long seed;
    private Solution<Route> solution;


    public DecisionProcessState(Instance instance, long seed, Solution<Route> solution) {
        this.instance = instance;
        this.seed = seed;
        this.solution = solution;
    }

    public DecisionProcessState(Instance instance, long seed) { this(instance, seed, new Solution<>()); }

    // Getters
    public Instance getInstance() { return instance; }
    public long getSeed() { return seed; }
    public Solution<Route> getSolution() { return solution; }

    // Setters
    public void setSeed(long seed) { this.seed = seed; }

    /**
     * Reset a decision process state as the initial state.
     */
    public void reset() {
        // TODO
    }

    @Override
    public DecisionProcessState clone() {
        // TODO
        return null;
    }
}
