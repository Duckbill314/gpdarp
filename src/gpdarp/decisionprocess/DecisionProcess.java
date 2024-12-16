package gpdarp.decisionprocess;

import gpdarp.core.IdleRequest;
import gpdarp.core.Instance;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A decision process is in charge of executing a simulated sequence of events.
 * It includes
 *  - A decision process state: the state of the vehicles and the environment,
 *  - An event queue: the events to happen,
 *  - A waiting list: the list of requests that were not accepted,
 *  - An allocation policy: for allocating requests to vehicles.
 *
 * @author gphhucarp, William Huang
 */
public abstract class DecisionProcess {
    protected DecisionProcessState state;
    protected PriorityQueue<DecisionProcessEvent> eventQueue;
    protected List<IdleRequest> waitingList;
    protected VehiclePolicy vehiclePolicy;
    protected RequestPolicy requestPolicy;

    public DecisionProcess(DecisionProcessState state,
                           PriorityQueue<DecisionProcessEvent> eventQueue,
                           List<IdleRequest> waitingList,
                           VehiclePolicy vehiclePolicy,
                           RequestPolicy requestPolicy) {
        this.state = state;
        this.eventQueue = eventQueue;
        this.waitingList = waitingList;
        this.vehiclePolicy = vehiclePolicy;
        this.requestPolicy = requestPolicy;
    }

    // Getters
    public DecisionProcessState getState() {
        return state;
    }
    public PriorityQueue<DecisionProcessEvent> getEventQueue() {
        return eventQueue;
    }
    public List<IdleRequest> getWaitingList() { return waitingList; }
    public VehiclePolicy getVehiclePolicy() {
        return vehiclePolicy;
    }
    public RequestPolicy getRequestPolicy() { return requestPolicy; }

    /**
     * Add an event to the event queue.
     *
     * @param event the event to be added.
     */
    public void addEvent(DecisionProcessEvent event) { eventQueue.add(event); }

    /**
     * Add a request to the waiting list.
     *
     * @param request the request to be added.
     */
    public void addWaiting(IdleRequest request) { waitingList.add(request); }

    /**
     * Initialise a reactive decision process from an instance,
     * a vehicle allocation policy, and a request allocation policy.
     *
     * @param instance the given instance.
     * @param vehiclePolicy the vehicle allocation policy.
     * @param requestPolicy the request allocation policy.
     *
     * @return the initial reactive decision process.
     */
    public static ReactiveDecisionProcess initReactive(Instance instance,
                                                       VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy) {
        DecisionProcessState state = new DecisionProcessState(instance);
        PriorityQueue<DecisionProcessEvent> eventQueue = new PriorityQueue<>();
        List<IdleRequest> waitingList = new ArrayList<>();
        return new ReactiveDecisionProcess(state, eventQueue, waitingList, vehiclePolicy, requestPolicy);
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
