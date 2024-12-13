package gpdarp.decisionprocess;

import gpdarp.core.*;
import gpdarp.representation.Pool;
import gpdarp.representation.Solution;
import gpdarp.representation.route.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * A decision process state maintains high level state-based information to facilitate the decision process.
 * This information includes
 * - the instance (which contains most of the information),
 * - the current time,
 * - the solution,
 * - the pool of candidate allocations (mainly for ensemble policies).
 *
 * @author gphhucarp, William Huang
 */
public class DecisionProcessState {
    private final Instance instance;
    private int time;
    private final Solution solution;
    private Pool pool;

    public DecisionProcessState(Instance instance, int time, Solution solution, Pool pool) {
        this.instance = instance;
        this.time = time;
        this.solution = solution;
        this.pool = pool;
    }

    // Initialisation constructor
    public DecisionProcessState(Instance instance) { this(instance, 0, new Solution(), new Pool()); }

    // Getters
    public Instance getInstance() { return instance; }
    public int getTime() { return time; }
    public Solution getSolution() { return solution; }
    public Pool getPool() { return pool; }

    // Setters
    public void setTime(int time) { this.time = time; }
    public void setPool(Pool pool) { this.pool = pool; }

    /**
     * Update the solution with all the vehicles' current historical routes.
     */
    public void updateSolution() {
        List<Route> routes = new ArrayList<>();
        instance.getVehicles().forEach(v -> routes.add(v.getHistoricalRoute()));
        solution.setRoutes(routes);
    }

    /**
     * Resets the decision process state by resetting the instance and solution.
     */
    public void reset() {
        instance.reset();
        time = 0;
        solution.reset();
    }

    @Override
    public DecisionProcessState clone() {
        return new DecisionProcessState(instance.clone(), time, solution.clone(), pool.clone());
    }
}
