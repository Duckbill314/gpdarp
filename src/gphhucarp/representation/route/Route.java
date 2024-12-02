package gphhucarp.representation.route;

import gphhucarp.core.Position;

/**
 * An abstract class of a route.
 * It cannot serve the demand more than its capacity.
 *
 * @author gphhucarp, William Huang
 */
public abstract class Route {

    protected double capacity; // the capacity of the route
    protected double demand; // the total demand served by the route
    protected double cost; // the total cost/time of the route

    public Route(double capacity, double demand, double cost) {
        this.capacity = capacity;
        this.demand = demand;
        this.cost = cost;
    }

    // Getters
    public double getCapacity() {
        return capacity;
    }
    public double getDemand() {
        return demand;
    }
    public double getCost() {
        return cost;
    }

    // Setters
    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }
    public void setDemand(double demand) {
        this.demand = demand;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Return the current position of the route, i.e. the current position of the vehicle.
     * It is essentially the last position in the sequence.
     *
     * @return the current position of the route.
     */
    public abstract Position currPos();

    /**
     * Reset the sequence.
     */
    public abstract void reset();

    /**
     * Clone the route.
     * @return the cloned route.
     */
    public abstract Route clone();
}
