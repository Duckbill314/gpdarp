package gpdarp.gp.terminal.feature;

import gpdarp.core.Arc;
import gpdarp.core.Graph;
import gpdarp.core.Instance;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;

/**
 * The fullness of the closest feasible alternative route after serving the task.
 * If the candidate is the depot loop, return 0 (no need to consider alternative route).
 * If there is no alternative route, return infinity.
 */

public class FullnessAfterService1 extends FeatureGPNode {

    public FullnessAfterService1() {
        super();
        name = "FAS1";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Arc candidate = calcPriorityProblem.getCandidate();

        if (candidate.equals(calcPriorityProblem.getState().getInstance().getDepotLoop()))
            return 0;

        DecisionProcessState state = calcPriorityProblem.getState();
        Instance instance = state.getInstance();
        Graph graph = instance.getGraph();
        int currNode = calcPriorityProblem.getRoute().currPos();
        int depot = instance.getDepot();

        if (state.getRouteAdjacencyList(candidate).isEmpty())
            return Double.POSITIVE_INFINITY;

        NodeSeqRoute route1 = null;

        for (int i = 0; i < state.getRouteAdjacencyList(candidate).size(); i++) {
            route1 = state.getRouteAdjacencyList(candidate).get(i);

            // whether the alternative is feasible or not
            if (route1.getDemand() + candidate.getExpectedDemand() <= route1.getCapacity()) {
                // yes, feasible
                break;
            }
            else if (graph.getEstDistance(currNode, candidate.getFrom()) ==
                    graph.getEstDistance(currNode, depot) +
                            graph.getEstDistance(depot, candidate.getFrom())) {
                // pass depot, so can refill on the way
                break;
            }
        }

        // no alternative route is feasible
        if (route1 == null)
            return Double.POSITIVE_INFINITY;

        if (graph.getEstDistance(currNode, candidate.getFrom()) ==
                graph.getEstDistance(currNode, depot) +
                        graph.getEstDistance(depot, candidate.getFrom())) {
            return candidate.getExpectedDemand() / route1.getCapacity();
        }
        else {
            return (route1.getDemand() + candidate.getExpectedDemand()) / route1.getCapacity();
        }
    }
}
