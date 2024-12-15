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
    private double penalty;

    public Solution(List<Route> routes) {
        this.routes = Route.listClone(routes);
    }
    public Solution() {
        this(new ArrayList<>());
    }

    // Getters
    public List<Route> getRoutes() {
        return routes;
    }
    public Route getRoute(int index) {
        return routes.get(index);
    }
    public double getPenalty() { return penalty; }

    // Setters
    public void setRoutes(List<Route> routes) {
        this.routes = Route.listClone(routes);
    }
    public void setPenalty(double penalty) { this.penalty = penalty; }

    /**
     * Reset this solution by resetting each route.
     */
    public void reset() {
        routes.forEach(Route::reset);
    }

    /**
     * Calculate the total cost, which is the sum of the route costs.
     *
     * @return the total cost.
     */
    public double totalCost() {
        return routes.stream()
                .map(r -> r.getLength() + penalty * r.calculatePenalty())
                .reduce(0.0, Double::sum);
    }

    /**
     * Calculate the maximal route cost, i.e. makespan.
     *
     * @return the maximal route cost.
     */
    public double maxRouteCost() {
        return routes.stream()
                .map(r -> r.getLength() + penalty * r.calculatePenalty())
                .max(Double::compare)
                .orElse(0.0);
    }

    /**
     * Return the value of an objective, NaN if the objective cannot be calculated.
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
    public String toString() { return String.format("Routes: %s", routes); }

    @Override
    public Solution clone() { return new Solution(Route.listClone(routes)); }
}
