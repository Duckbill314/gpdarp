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

    public Solution(List<Route> routes) {
        this.routes = routes;
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

    // Setters
    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    /**
     * Add a route into the solution.
     *
     * @param route the added route.
     */
    public void addRoute(Route route) {
        routes.add(route);
    }

    /**
     * Remove the route with an index.
     *
     * @param index the index of the route to be removed.
     */
    public void removeRoute(int index) {
        routes.remove(index);
    }

    /**
     * Reset this solution by resetting each route.
     */
    public void reset() {
        for (Route route : routes) {
            route.reset();
        }
    }

    /**
     * Calculate the total cost, which is the sum of the route costs.
     *
     * @return the total cost.
     */
    public double totalCost() {
        double result = 0;
        for (Route route : routes) {
            result += route.getCost();
        }
        return result;
    }

    /**
     * Calculate the maximal route cost, i.e. makespan.
     *
     * @return the maximal route cost.
     */
    public double maxRouteCost() {
        double result = -1;
        for (Route route : routes) {
            if (result < route.getCost())
                result = route.getCost();
        }
        return result;
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
