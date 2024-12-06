package gpdarp.decisionprocess.proreactive;

import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.RoutingPolicy;
import gpdarp.decisionprocess.proreactive.event.ProreactiveServingEvent;
import gpdarp.representation.route.Route;

import java.util.PriorityQueue;

/**
 * A proactive-reactive decision process produces an actual solution based on
 * a planned solution (task sequence routes) and a routing policy.
 * Whenever a vehicle finishes a task, the routing policy takes the next task
 * and decides whether to continue the service or go back to the depot to refill.
 */

public class ProreativeDecisionProcess extends DecisionProcess {

    public ProreativeDecisionProcess(DecisionProcessState state,
                                     PriorityQueue<DecisionProcessEvent> eventQueue,
                                     RoutingPolicy routingPolicy,
                                     Route plan) {
        super(state, eventQueue, routingPolicy, plan);
    }

    @Override
    public void reset() {
        state.reset();
        eventQueue.clear();
        for (int i = 0; i < plan.getRoutes().size(); i++)
            eventQueue.add(new ProreactiveServingEvent(0,
                    state.getSolution().getRoute(i), plan.getRoute(i), 0));
    }

    @Override
    protected ProreativeDecisionProcess clone() {
        DecisionProcessState clonedState = state.clone();
        PriorityQueue<DecisionProcessEvent> clonedEQ = new PriorityQueue<>(eventQueue);
        Route clonedPlan = plan.clone();

        return new ProreativeDecisionProcess(clonedState, clonedEQ, routingPolicy, clonedPlan);
    }
}
