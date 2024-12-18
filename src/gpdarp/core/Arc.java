package gpdarp.core;

import gpdarp.decisionprocess.DecisionProcessState;

import java.util.ArrayList;
import java.util.List;

/**
 * An arc is a directed edge between two nodes. It has:
 *  - (from, to) nodes,
 *  - length.
 *
 * @author William Huang
 */
public record Arc(Node from, Node to, int length) implements Comparable<Arc> {
    // Simplified default constructor
    public Arc(Node from, Node to) {
        this(from, to, from.calcDist(to));
    }

    @Override
    public String toString() {
        return String.format("(%s, %s): sc = %d", from, to, length);
    }

    @Override
    public int compareTo(Arc o) { return Double.compare(length, o.length()); }

    @Override
    public Arc clone() { return new Arc(from.clone(), to.clone()); }

    /**
     * Updates the estimated arrival times for the nodes in the arc.
     *
     * @param state the state of the decision process.
     * @param vehicle the vehicle associated with the route.
     */
    public void updateEtas(DecisionProcessState state, Vehicle vehicle) {
        Instance instance = state.getInstance();

        int startTime;
        int pickupTime;
        int serveTime;

        switch (from.getType()) {
            case IDLE -> {
                startTime = from.getTime();
                to.setTime(startTime + instance.calculateTravelTime(length));
            }
            case PICKUP -> {
                startTime = from.getTime();
                pickupTime = from.getRequest().getTEarly();
                if (startTime < pickupTime) {
                    startTime = pickupTime;
                }
                serveTime = vehicle.getServeTime();
                to.setTime(startTime + serveTime + instance.calculateTravelTime(length));
            }
        }
    }

    /**
     * Utility method for creating deep clones of ArrayLists of Arcs.
     *
     * @param arcs the list of arcs to be cloned.
     *
     * @return the cloned list.
     */
    public static List<Arc> listClone(List<Arc> arcs) {
        List<Arc> clonedArcs = new ArrayList<>();
        for (Arc arc : arcs) {
            clonedArcs.add(arc.clone());
        }
        return clonedArcs;
    }
}
