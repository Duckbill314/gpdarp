package gphhucarp.representation.route;

import gphhucarp.core.Arc;
import gphhucarp.core.Instance;
import gphhucarp.core.Position;

import java.util.LinkedList;
import java.util.List;

/**
 * A task sequence route is a sequence of tasks.
 * Its total demand cannot exceed its capacity.
 *
 * @author gphhucarp, William Huang
 */
public class TaskSeqRoute extends Route {
    protected List<Arc> taskSequence;
    protected Position currNode;

    public TaskSeqRoute(double capacity, double demand, double cost,
                        List<Arc> taskSequence, Position currNode) {
        super(capacity, demand, cost);
        this.taskSequence = taskSequence;
        this.currNode = currNode;
    }

    // Initial sequence constructor
    public TaskSeqRoute(double capacity, Position initPos) {
        this(capacity, 0, 0, new LinkedList<>(), initPos);
    }

    // Getters
    public List<Arc> getTaskSequence() {
        return taskSequence;
    }
    public Arc get(int index) {
        return taskSequence.get(index);
    }
    public int size() {
        return taskSequence.size();
    }

    /**
     * Add a task to the end of the task sequence route.
     *
     * @param task the task to be added.
     */
    public void add(Arc task) {
        taskSequence.add(task);
        demand += 1;
        cost += task.serveCost();
        currNode = task.to();
    }

    @Override
    public void reset() {
        demand = 0;
        cost = 0;
        taskSequence.clear();
    }

    @Override
    public Position currPos() {
        return currNode;
    }

    public void setCurrNode(Position node) {
        this.currNode = node;
    }

    @Override
    public String toString() {
        String str = taskSequence.get(0).toString();

        for (int i = 1; i < taskSequence.size(); i++) {
            str += " -> " + taskSequence.get(i).toString();
        }

        return str;
    }

    @Override
    public Route clone() { return new TaskSeqRoute(capacity, demand, cost, new LinkedList<>(taskSequence), currNode); }
}
