package gpdarp.decisionprocess;

import gpdarp.core.*;
import gpdarp.representation.Solution;
import gpdarp.representation.route.Route;

/**
 * A decision process state maintains high level state-based information to facilitate the decision process.
 * This information includes
 * - the instance (which contains most of the information),
 * - the solution.
 *
 * @author gphhucarp, William Huang
 */
public class DecisionProcessState {
    private Instance instance;
    private Solution solution;

    public DecisionProcessState(Instance instance, Solution solution) {
        this.instance = instance;
        this.solution = solution;
    }

    // Initialisation constructor
    public DecisionProcessState(Instance instance) { this(instance, new Solution()); }

    // Getters
    public Instance getInstance() { return instance; }
    public Solution getSolution() { return solution; }

    /**
     * Resets the decision process state by resetting the instance and solution.
     */
    public void reset() {
        instance.reset();
        solution.reset();
    }

    @Override
    public DecisionProcessState clone() { return new DecisionProcessState(instance, solution); }
}
