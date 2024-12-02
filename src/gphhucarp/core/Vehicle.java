package gphhucarp.core;

import gphhucarp.representation.route.NodeSeqRoute;
import gphhucarp.representation.route.TaskSeqRoute;

/**
 * A vehicle is a defined construct with a set of important properties.
 * Most importantly, it keeps track of its route, which is relevant to the final solution.
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
    private final double serveTime;
    private Position pos;
    private NodeSeqRoute nodeSeq;
    private TaskSeqRoute taskSeq;

    public Vehicle(int id, int capacity, double chargeMax, double chargeState, double chargeFillRate,
                   double chargeDepletionRate, double serveTime, Position pos) {
        this.id = id;
        this.capacity = capacity;
        this.chargeMax = chargeMax;
        this.chargeState = chargeState;
        this.chargeFillRate = chargeFillRate;
        this.chargeDepletionRate = chargeDepletionRate;
        this.serveTime = serveTime;
        this.pos = pos;
        // TODO: initialise nodeSeq and taskSeq implicitly
    }

    // Getters
    public int getId() { return id; }
    public int getCapacity() { return this.capacity; }
    public double getChargeMax() { return this.chargeMax; }
    public double getChargeState() { return this.chargeState; }
    public double getChargeFillRate() { return this.chargeFillRate; }
    public double getChargeDepletionRate() { return this.chargeDepletionRate; }
    public double getServeTime() { return this.serveTime; }
    public Position getPos() { return this.pos; }
    public NodeSeqRoute getNodeSeq() { return this.nodeSeq; }
    public TaskSeqRoute getTaskSeq() { return this.taskSeq; }

    // Setters
    public void setChargeState(double chargeState) { this.chargeState = chargeState; }
    public void setPos(Position pos) { this.pos = pos; }
    public void setNodeSeq(NodeSeqRoute nodeSeq) { this.nodeSeq = nodeSeq; }
    public void setTaskSeq(TaskSeqRoute taskSeq) { this.taskSeq = taskSeq; }

    /**
     * Restore charge at a rate proportional to the elapsed time.
     * Charge cannot exceed maximum.
     *
     * @param tElapsed the time spent charging.
     */
    public void fill(double tElapsed) {
        chargeState += chargeFillRate * tElapsed;
        if (chargeState > chargeMax) {
            chargeState = chargeMax;
        }
    }

    /**
     * Deplete charge at a rate proportional to the elapsed time.
     * Charge should not fall below empty - however, this function allows it for the sake of determining feasibility.
     *
     * @param tElapsed the time spent charging.
     */
    public double deplete(double tElapsed) {
        chargeState -= chargeDepletionRate * tElapsed;
        return chargeState;
    }

    @Override
    public String toString() { return String.format("Vehicle %d | charge: %f/%f", id, chargeState, chargeMax); }
}
