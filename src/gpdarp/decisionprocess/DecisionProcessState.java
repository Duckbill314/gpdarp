package gpdarp.decisionprocess;

import gpdarp.core.*;
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
 * - the current waiting list of requests (for the purpose of evaluating terminals only).
 *
 * @author gphhucarp, William Huang
 */
public class DecisionProcessState {
    private final Instance instance;
    private int time;
    private final Solution solution;
    private List<Request> waitingList;

    public DecisionProcessState(Instance instance, int time, Solution solution) {
        this.instance = instance;
        this.time = time;
        this.solution = solution;
        this.solution.setLatenessPenalty(instance.getLatenessPenalty());
    }

    // Initialisation constructor
    public DecisionProcessState(Instance instance) { this(instance, 0, new Solution()); }

    // Getters
    public Instance getInstance() { return instance; }
    public int getTime() { return time; }
    public Solution getSolution() { return solution; }
    public List<Request> getWaitingList() { return waitingList; }

    // Setters
    public void setTime(int time) { this.time = time; }
    public void setWaitingList(List<Request> waitingList) { this.waitingList = waitingList; }

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
}
