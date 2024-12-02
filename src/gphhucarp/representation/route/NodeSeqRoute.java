package gphhucarp.representation.route;

import gphhucarp.core.Arc;
import gphhucarp.core.Instance;
import gphhucarp.core.Position;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A node sequence route is a sequence of nodes (positions).
 * This class maintains a node sequence, and keeps track of its next task.
 *
 * @author gphhucarp, William Huang
 */
public class NodeSeqRoute extends Route {
    private List<Position> nodeSequence;

    // fields used during the decision process
    private Arc nextTask; // the next task to serve

    public NodeSeqRoute(double capacity, double demand, double cost, List<Position> nodeSequence) {
        super(capacity, demand, cost);
        this.nodeSequence = nodeSequence;
    }

    // Initial sequence constructor
    public NodeSeqRoute(double capacity, Position initPos) {
        this(capacity, 0, 0, new LinkedList<Position>(Arrays.asList(initPos)));
    }

    // Getters
    public List<Position> getNodeSequence() {
        return nodeSequence;
    }
    public Position getNode(int index) {
        return nodeSequence.get(index);
    }
    public Arc getNextTask() {
        return nextTask;
    }

    // Setters
    public void setNextTask(Arc nextTask) {
        this.nextTask = nextTask;
    }

    /**
     * Update the route by adding a node.
     *
     * @param node the node.
     */
    public void add(Position node) {
        nodeSequence.add(node);
        Arc arc = new Arc(currPos(), node);
        demand += 1;
        cost += arc.serveCost();
    }

    @Override
    public void reset() {
        demand = 0;
        cost = 0;
        Position currPos = currPos();
        nodeSequence.clear();
        nodeSequence.add(currPos);
    }

    @Override
    public Position currPos() {
        return nodeSequence.get(nodeSequence.size()-1);
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < nodeSequence.size(); i++) {
            str += getNode(i);
        }
        return str;
    }

    /**
     * Clone the node sequence route.
     * @return the cloned route.
     */
    @Override
    public Route clone() {
        List<Position> clonedNodeSeq = new LinkedList<>(nodeSequence);

        NodeSeqRoute cloned = new NodeSeqRoute(capacity, demand, cost, clonedNodeSeq);
        cloned.setNextTask(nextTask);

        return cloned;
    }
}
