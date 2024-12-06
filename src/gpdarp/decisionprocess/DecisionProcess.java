package gpdarp.decisionprocess;

import gpdarp.core.Instance;
import gpdarp.decisionprocess.proreactive.ProreativeDecisionProcess;
import gpdarp.decisionprocess.proreactive.event.ProreactiveServingEvent;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;
import gpdarp.decisionprocess.reactive.event.ReactiveRefillEvent;
import gpdarp.representation.route.Route;

import java.util.PriorityQueue;

/**
 * An abstract of a decision process. A decision process is a process where
 * vehicles make decisions as they go to serve the tasks of the graph.
 * It includes
 *  - A decision process state: the state of the vehicles and the environment.
 *  - An event queue: the events to happen.
 *  - A routing policy that makes decisions as the vehicles go.
 *  - A task sequence solution as a predefined plan. This is used for proactive-reactive decision process.
 */

public abstract class DecisionProcess {
    protected DecisionProcessState state; // the state
    protected PriorityQueue<DecisionProcessEvent> eventQueue;
    protected RoutingPolicy routingPolicy;

    public DecisionProcess(DecisionProcessState state,
                           PriorityQueue<DecisionProcessEvent> eventQueue,
                           RoutingPolicy routingPolicy) {
        this.state = state;
        this.eventQueue = eventQueue;
        this.routingPolicy = routingPolicy;
    }

    public DecisionProcessState getState() {
        return state;
    }

    public PriorityQueue<DecisionProcessEvent> getEventQueue() {
        return eventQueue;
    }

    public RoutingPolicy getRoutingPolicy() {
        return routingPolicy;
    }

    public void setRoutingPolicy(RoutingPolicy routingPolicy) {
        this.routingPolicy = routingPolicy;
    }

    /**
     * Initialise a reactive decision process from an instance and a routing policy.
     * @param instance the given instance.
     * @param seed the seed to sample the random variables.
     * @param routingPolicy the given policy.
     * @return the initial reactive decision process.
     */
    public static ReactiveDecisionProcess initReactive(Instance instance,
                                                       long seed,
                                                       RoutingPolicy routingPolicy) {
        DecisionProcessState state = new DecisionProcessState(instance, seed);
        PriorityQueue<DecisionProcessEvent> eventQueue = new PriorityQueue<>();
        for (Route route : state.getSolution().getRoutes())
            eventQueue.add(new ReactiveRefillEvent(0, route));

        return new ReactiveDecisionProcess(state, eventQueue, routingPolicy);
    }

    /**
     * Run the decision process.
     */
    public void run() {

        // trigger the events.
        while (!eventQueue.isEmpty()) {
            DecisionProcessEvent event = eventQueue.poll();
            event.trigger(this);
        }
    }

    /**
     * Reset the decision process.
     * This is done by reseting the decision process state and event queue.
     */
    public abstract void reset();
}
