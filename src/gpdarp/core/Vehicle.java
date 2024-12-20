package gpdarp.core;

import gpdarp.representation.route.Route;

import java.util.*;

/**
 * A vehicle is a defined construct with a set of important properties, including:
 * - capacity,
 * - battery charging information,
 * - service time for request pickup and dropoff,
 * - the latest known idle position (implies that the vehicle is not busy, and available to accept a request),
 * - full route history.
 * It also has a temporary priority value for the purpose of request allocation.
 *
 * @author William Huang
 */
public class Vehicle {
    private final int id;
    private final int capacity;
    private final double chargeMax;
    private double chargeState;
    private final double chargeFillRate;
    private final double chargeDepletionRate;
    private final int serveTime;
    private Node currPos;
    private Route route;
    private double priority;

    public Vehicle(int id, int capacity, double chargeMax, double chargeState, double chargeFillRate,
                   double chargeDepletionRate, int serveTime, Node currPos,
                   Route route, double priority) {
        this.id = id;
        this.capacity = capacity;
        this.chargeMax = chargeMax;
        this.chargeState = chargeState;
        this.chargeFillRate = chargeFillRate;
        this.chargeDepletionRate = chargeDepletionRate;
        this.serveTime = serveTime;
        this.currPos = currPos;
        this.route = route;
        this.priority = priority;
    }

    // Getters
    public int getId() { return id; }
    public int getCapacity() { return capacity; }
    public double getChargeMax() { return chargeMax; }
    public double getChargeState() { return chargeState; }
    public double getChargeFillRate() { return chargeFillRate; }
    public double getChargeDepletionRate() { return chargeDepletionRate; }
    public int getServeTime() { return serveTime; }
    public Node getCurrPos() { return currPos; }
    public Route getRoute() { return route; }
    public double getPriority() { return priority; }
    public boolean isBusy() { return currPos == null; }

    // Setters
    public void setChargeState(double chargeState) { this.chargeState = chargeState; }
    public void setCurrPos(Node currPos) { this.currPos = currPos; }
    public void setRoute(Route route) { this.route = route; }
    public void setPriority(double priority) { this.priority = priority; }

    /**
     * Helper method for handling charging events.
     * Because a vehicle cannot serve requests while it is on the way to a charging station or while it is
     * charging, the time taken for each can be summed to calculate the next available time,
     * which occurs after the vehicle has finished charging.
     *
     * @param instance the instance of the problem.
     * @param request the selected charging "request".
     * @return the point of the next available time and position.
     */
    public Node charge(Instance instance, Request request) {
        setCurrPos(null);

        Node startPoint = request.getPickup();
        Station station = (Station) request.getDropoff();

        Arc toStation = new Arc(startPoint, station);
        toStation.updateEtas(instance, this);
        deplete(toStation.length());
        route.push(toStation);

        int chargeTime = estimateFillTime();
        fill(chargeTime);

        int nextIdleTime = station.getTime() + chargeTime;
        return station.idleClone(nextIdleTime);
    }

    /**
     * Helper method for handling request service events.
     * Because a vehicle cannot accept a new request while it is serving its current one, the time taken for each of
     * pickup and dropoff can be summed to calculate the next available time,
     * which occurs after the vehicle has finished servicing the dropoff.
     *
     * @param instance the instance of the problem.
     * @param request the selected charging "request".
     * @param startPoint the idle point that the vehicle begins the service at.
     * @return the point of the next available time and position.
     */
    public Node serveRequest(Instance instance, Request request, Node startPoint) {
        setCurrPos(null);

        Node pickup = request.getPickup();
        Node dropoff = request.getDropoff();

        Arc toPickup = new Arc(startPoint, pickup);
        toPickup.updateEtas(instance, this);
        deplete(toPickup.length());
        route.push(toPickup);

        Arc toDropoff = new Arc(pickup, dropoff);
        toDropoff.updateEtas(instance, this);
        deplete(toDropoff.length());
        route.push(toDropoff);

        int nextIdleTime = dropoff.getTime() + getServeTime();
        return dropoff.idleClone(nextIdleTime);
    }

    /**
     * Restore charge at a rate proportional to the elapsed time.
     * Charge cannot exceed maximum.
     *
     * @param tElapsed the time spent charging.
     */
    public void fill(int tElapsed) {
        chargeState += chargeFillRate * tElapsed;
        if (chargeState > chargeMax) {
            chargeState = chargeMax;
        }
    }

    /**
     * Estimates the time required to fully charge.
     *
     * @return the estimated charge time.
     */
    public int estimateFillTime() {
        double missing = chargeMax - chargeState;
        return (int) Math.ceil(missing / chargeFillRate);
    }

    /**
     * Deplete charge at a rate proportional to a travelled length.
     * Charge should not fall below empty, but the safeguard in place is only there for potential rounding errors.
     * For proper functionality, depletion actions should always be checked for feasibility before they are taken.
     *
     * @param length the length travelled.
     */
    public void deplete(int length) {
        chargeState -= chargeDepletionRate * length;
        if (chargeState < 0) {
            chargeState = 0;
        }
    }

    /**
     * Estimation of resulting charge from travelling a given length.
     * Always check using this method before actually depleting!
     *
     * @param length the length travelled.
     * @return the resulting charge state.
     */
    public double estimateDepletion(int length) { return chargeState - chargeDepletionRate * length; }

    @Override
    public String toString() { return String.format("Vehicle %d | charge: %f/%f", id, chargeState, chargeMax); }

    @Override
    public Vehicle clone() {
        return new Vehicle(id, capacity, chargeMax, chargeState, chargeFillRate, chargeDepletionRate,
                serveTime, currPos.clone(), route.clone(), priority);
    }

    /**
     * Utility method for creating deep clones of ArrayLists of Vehicles.
     *
     * @param vehicles the list of vehicles to be cloned.
     * @return the cloned list.
     */
    public static List<Vehicle> listClone(List<Vehicle> vehicles) {
        List<Vehicle> clonedVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            clonedVehicles.add(vehicle.clone());
        }
        return clonedVehicles;
    }

    /**
     * Compare the vehicle to another vehicle on the basis of their id number.
     * This is a quite meaningless natural comparator, because other more meaningful comparisons are made explicitly
     * whenever necessary.
     *
     * @param o the other vehicle.
     * @return priority of this vehicle in comparison to the other vehicle.
     */
    public int compareTo(Vehicle o) { return (id - o.getId()); }
}
