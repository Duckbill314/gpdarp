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

public class Solution<T extends Route> {
    private List<T> routes;

    public Solution(List<T> routes) {
        this.routes = routes;
    }
    public Solution() {
        this(new ArrayList<>());
    }

    // Getters
    public List<T> getRoutes() {
        return routes;
    }
    public T getRoute(int index) {
        return routes.get(index);
    }

    // Setters
    public void setRoutes(List<T> routes) {
        this.routes = routes;
    }

    /**
     * Add a route into the solution.
     *
     * @param route the added route.
     */
    public void addRoute(T route) {
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
        for (T route : routes) {
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
        for (T route : routes) {
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
        for (T route : routes) {
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
        switch (objective) {
            case TOTAL_COST:
                return totalCost();
            case MAX_ROUTE_COST:
                return maxRouteCost();
            default:
                return Double.NaN;
        }
    }

    @Override
    public String toString() {
        // TODO
        return null;
    }

    public Solution<T> clone() {
        // TODO
        return null;
    }
}
