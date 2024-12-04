package gpdarp.decisionprocess.reactive;

import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.RoutingPolicy;
import gpdarp.decisionprocess.reactive.event.ReactiveRefillEvent;

import java.util.PriorityQueue;

/**
 * The reactive decision process builds the solution in real time.
 * It is actually a decision process that adds one task at the end of the current route at a time.
 * It assumes that the actual serving/deadheading cost of an arc is realised after it is served/traversed.
 * The actual demand of a task is realised after it is served.
 *
 * Created by gphhucarp on 25/08/17.
 */
public class ReactiveDecisionProcess extends DecisionProcess {

    public ReactiveDecisionProcess(DecisionProcessState state,
                                   PriorityQueue<DecisionProcessEvent> eventQueue,
                                   RoutingPolicy routingPolicy) {
        super(state, eventQueue, routingPolicy, null);
    }

    @Override
    public void reset() {
        state.reset();
        eventQueue.clear();
        for (NodeSeqRoute route : state.getSolution().getRoutes())
            eventQueue.add(new ReactiveRefillEvent(0, route));
    }

    @Override
    protected ReactiveDecisionProcess clone() {
        DecisionProcessState clonedState = state.clone();
        PriorityQueue<DecisionProcessEvent> clonedEQ = new PriorityQueue<>(eventQueue);

        return new ReactiveDecisionProcess(clonedState, clonedEQ, routingPolicy);
    }
}
