package gpdarp.decisionprocess;

import gpdarp.core.Instance;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;

import java.util.PriorityQueue;

/**
 * A decision process is in charge of executing a simulated sequence of events.
 * It includes
 *  - A decision process state: the state of the vehicles and the environment,
 *  - An event queue: the events to happen,
 *  - An allocation policy: for allocating requests to vehicles.
 *
 * @author gphhucarp, William Huang
 */
public abstract class DecisionProcess {
    protected DecisionProcessState state;
    protected PriorityQueue<DecisionProcessEvent> eventQueue;
    protected AllocationPolicy allocationPolicy;

    public DecisionProcess(DecisionProcessState state,
                           PriorityQueue<DecisionProcessEvent> eventQueue,
                           AllocationPolicy allocationPolicy) {
        this.state = state;
        this.eventQueue = eventQueue;
        this.allocationPolicy = allocationPolicy;
    }

    // Getters
    public DecisionProcessState getState() {
        return state;
    }
    public PriorityQueue<DecisionProcessEvent> getEventQueue() {
        return eventQueue;
    }
    public AllocationPolicy getAllocationPolicy() {
        return allocationPolicy;
    }

    // Setters
    public void setAllocationPolicy(AllocationPolicy allocationPolicy) {
        this.allocationPolicy = allocationPolicy;
    }

    /**
     * Add an event to the event queue.
     *
     * @param event the event to be added.
     */
    public void addEvent(DecisionProcessEvent event) { eventQueue.add(event); }

    /**
     * Initialise a reactive decision process from an instance and an allocation policy.
     *
     * @param instance the given instance.
     * @param allocationPolicy the given policy.
     *
     * @return the initial reactive decision process.
     */
    public static ReactiveDecisionProcess initReactive(Instance instance, AllocationPolicy allocationPolicy) {
        DecisionProcessState state = new DecisionProcessState(instance);
        PriorityQueue<DecisionProcessEvent> eventQueue = new PriorityQueue<>();
        return new ReactiveDecisionProcess(state, eventQueue, allocationPolicy);
    }

    /**
     * Run the decision process.
     */
    public void run() {
        while (!eventQueue.isEmpty()) {
            DecisionProcessEvent event = eventQueue.poll();
            state.setTime(event.getTime());
            event.trigger(this);
            state.updateSolution();
        }
    }

    /**
     * Reset the decision process.
     * This is done by resetting the decision process state and event queue.
     */
    public abstract void reset();
}
