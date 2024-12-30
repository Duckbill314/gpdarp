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
    private double travelTimeRate;
    private double latenessPenalty;
    private boolean feasible;

    public Solution(List<Route> routes) {
        this.routes = Route.listClone(routes);
        this.feasible = false;
    }

    public Solution() {
        this(new ArrayList<>());
    }

    // Getters
    public List<Route> getRoutes() {
        return routes;
    }
    public double getTravelTimeRate() { return travelTimeRate; }
    public double getLatenessPenalty() { return latenessPenalty; }
    public boolean isFeasible() { return feasible; }

    // Setters
    public void setRoutes(List<Route> routes) {
        this.routes = Route.listClone(routes);
    }
    public void setTravelTimeRate(double travelTimeRate) { this.travelTimeRate = travelTimeRate; }
    public void setLatenessPenalty(double latenessPenalty) { this.latenessPenalty = latenessPenalty; }
    public void setFeasible(boolean feasible) { this.feasible = feasible; }

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
        return routes.stream()
                .map(r -> r.getTime() + latenessPenalty * r.calculatePenalty())
                .reduce(0.0, Double::sum);
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
        return routes.stream()
                .map(r -> r.getTime() + latenessPenalty * r.calculatePenalty())
                .max(Double::compare)
                .orElse(0.0);
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
            default -> Double.NaN;
        };
    }

    @Override
    public String toString() {
        String str = "\nRoutes: \n";
        for (Route route : routes) {
            str += String.format("%s\n", route);
        }
        return str;
    }

    @Override
    public Solution clone() {
        Solution clone = new Solution(Route.listClone(routes));
        clone.travelTimeRate = travelTimeRate;
        clone.latenessPenalty = latenessPenalty;
        clone.feasible = feasible;
        return clone;
    }
}
