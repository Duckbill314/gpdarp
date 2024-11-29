package gphhucarp.core;

import gphhucarp.representation.route.NodeSeqRoute;
import gphhucarp.representation.route.TaskSeqRoute;

public class Vehicle {
    private int id;
    private int capacity;
    private double chargeMax;
    private double chargeState;
    private double chargeFillRate;
    private double chargeDepletionRate;
    private double serveTime;
    private NodeSeqRoute nodeSeq;
    private TaskSeqRoute taskSeq;

    public Vehicle(int id,
                   int capacity,
                   double chargeMax,
                   double chargeState,
                   double chargeFillRate,
                   double chargeDepletionRate,
                   double serveTime,
                   NodeSeqRoute nodeSeq,
                   TaskSeqRoute taskSeq) {
        this.id = id;
        this.capacity = capacity;
        this.chargeMax = chargeMax;
        this.chargeState = chargeState;
        this.chargeFillRate = chargeFillRate;
        this.chargeDepletionRate = chargeDepletionRate;
        this.serveTime = serveTime;
        this.nodeSeq = nodeSeq;
        this.taskSeq = taskSeq;
    }

    // Getters
    public int getId() { return id; }
    public int getCapacity() { return this.capacity; }
    public double getChargeMax() { return this.chargeMax; }
    public double getChargeState() { return this.chargeState; }
    public double getChargeFillRate() { return this.chargeFillRate; }
    public double getChargeDepletionRate() { return this.chargeDepletionRate; }
    public double getServeTime() { return this.serveTime; }
    public NodeSeqRoute getNodeSeq() { return this.nodeSeq; }
    public TaskSeqRoute getTaskSeq() { return this.taskSeq; }

    // Setters
    public void setChargeState(double chargeState) { this.chargeState = chargeState; }
    public void setNodeSeq(NodeSeqRoute nodeSeq) { this.nodeSeq = nodeSeq; }
    public void setTaskSeq(TaskSeqRoute taskSeq) { this.taskSeq = taskSeq; }

    // Manipulators
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
