package gpdarp.representation;

import gpdarp.core.Objective;
import gpdarp.representation.route.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * A solution is represented as an aggregation of vehicles' historical routes.
 *
 * @author gphhucarp, William Huang
 */

public class Solution {
    private List<Route> routes;
    private double latenessPenalty;
    private boolean feasible;
    private int numRequests;
    private double avgDecisionTime;
    private String name;

    public Solution(List<Route> routes) {
        this.routes = routes;
        this.feasible = false;
    }

    public Solution() {
        this(new ArrayList<>());
    }

    // Getters
    public List<Route> getRoutes() { return routes; }
    public double getLatenessPenalty() { return latenessPenalty; }
    public boolean isFeasible() { return feasible; }
    public int getNumRequests() { return numRequests; }
    public double getAvgDecisionTime() { return avgDecisionTime; }
    public String getName() { return name; }

    // Setters
    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
    public void setLatenessPenalty(double latenessPenalty) { this.latenessPenalty = latenessPenalty; }
    public void setFeasible(boolean feasible) { this.feasible = feasible; }
    public void setNumRequests(int numRequests) { this.numRequests = numRequests; }
    public void setAvgDecisionTime(double avgDecisionTime) { this.avgDecisionTime = avgDecisionTime; }
    public void setName(String name) { this.name = name; }

    /**
     * Reset this solution by resetting each route.
     */
    public void reset() {
        routes.forEach(Route::reset);
        feasible = false;
    }

    /**
     * Calculate the total cost, which is the sum of the route costs.
     *
     * @return the total cost.
     */
    public double totalCost() {
        if (!feasible) {
            return Double.POSITIVE_INFINITY;
        }

        double time = routes.stream()
                .map(r -> (double) r.getTime())
                .reduce(0.0, Double::sum);

        double penalty = routes.stream()
                .map(r -> latenessPenalty * r.calculatePenalty())
                .reduce(0.0, Double::sum);

        double cost = time + penalty;

        // System.out.printf("Total time/penalty: %.1f/%.1f%n", time, penalty);
        return cost;
    }

    /**
     * Calculate the maximal route cost, i.e. makespan.
     *
     * @return the maximal route cost.
     */
    public double maxRouteCost() {
        if (!feasible) {
            return Double.POSITIVE_INFINITY;
        }

        Route route = routes.stream()
                .max((r1, r2) -> {
                    double cost1 = r1.getTime() + latenessPenalty * r1.calculatePenalty();
                    double cost2 = r2.getTime() + latenessPenalty * r2.calculatePenalty();
                    return Double.compare(cost1, cost2);
                })
                .orElse(null);

        if (route == null) {
            return Double.POSITIVE_INFINITY;
        }

        double time = route.getTime();
        double penalty = latenessPenalty * route.calculatePenalty();
        double cost = time + penalty;

        // System.out.printf("Highest cost route's time/penalty: %.1f/%.1f%n", time, penalty);
        return cost;
    }

    /**
     * Return the value of an objective, NaN if the objective cannot be calculated.
     *
     * @param objective the objective.
     * @return the objective value of the solution.
     */
    public double objValue(Objective objective) {
        return switch (objective) {
            case TOTAL_COST -> totalCost();
            case MAX_ROUTE_COST -> maxRouteCost();
        };
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("\nRoutes: \n");
        for (Route route : routes) {
            str.append(String.format("%s\n", route));
        }
        return str.toString();
    }
}
