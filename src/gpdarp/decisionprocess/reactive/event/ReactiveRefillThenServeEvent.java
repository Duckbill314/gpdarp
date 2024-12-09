package gpdarp.decisionprocess.reactive.event;

import gpdarp.core.Arc;
import gpdarp.core.Graph;
import gpdarp.core.Instance;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.AllocationPolicy;

/**
 * The reactive refill-then-serve event occurs after a route failure occurs.
 * The vehicle is on the way back to the depot to refill, and then coming back
 * to finish the failed service.
 * The target node is the depot, and the next task is the failed task.
 */

public class ReactiveRefillThenServeEvent extends DecisionProcessEvent {

    private NodeSeqRoute route;
    private Arc nextTask;

    public ReactiveRefillThenServeEvent(double time,
                                        NodeSeqRoute route, Arc nextTask) {
        super(time);
        this.route = route;
        this.nextTask = nextTask;
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        AllocationPolicy policy = decisionProcess.getAllocationPolicy();
        DecisionProcessState state = decisionProcess.getState();
        Instance instance = state.getInstance();
        Graph graph = instance.getGraph();
        int depot = instance.getDepot();

        int currNode = route.currPos();

        if (currNode == depot) {
            // refill when arriving the depot
            route.setDemand(0);

            // now going back to continue the failed service
            // this is essentially a reactive serving event
            decisionProcess.getEventQueue().add(
                    new ReactiveServingEvent(route.getCost(), route, nextTask));
        }
        else {
            // continue going to the depot if not arrived yet
            int nextNode = graph.getPathTo(currNode, depot);

            // check the accessibility of all the arcs going out from the arrived node.
            boolean edgeFailure = false; // edge failure: next node is not accessible.
            for (Arc arc : graph.getOutNeighbour(currNode)) {
                if (instance.getActDeadheadingCost(arc) == Double.POSITIVE_INFINITY) {
                    graph.updateEstCostMatrix(arc.getFrom(), arc.getTo(), Double.POSITIVE_INFINITY);

                    if (arc.getTo() == nextNode)
                        edgeFailure = true;
                }
            }

            // recalculate the shortest path based on the new cost matrix.
            // update the next node in the new shortest path.
            if (edgeFailure) {
                graph.recalcEstDistanceBetween(currNode, depot);
                nextNode = graph.getPathTo(currNode, depot);
            }

            // add the traverse to the next node
            route.add(nextNode, 0, instance);
            // add a new event
            decisionProcess.getEventQueue().add(
                    new ReactiveRefillThenServeEvent(route.getCost(), route, nextTask));
        }
    }
}
