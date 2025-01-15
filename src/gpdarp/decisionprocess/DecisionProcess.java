package gpdarp.decisionprocess;

import gpdarp.core.Request;
import gpdarp.core.Instance;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;
import gpdarp.decisionprocess.reactive.event.ReactiveRequestEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A decision process is in charge of executing a simulated sequence of events.
 * It includes
 *  - A decision process state: the state of the vehicles and the environment,
 *  - An event queue: the events to happen,
 *  - A waiting list: the list of requests that were not accepted,
 *  - A vehicle allocation policy: for selecting a vehicle to allocate a request to,
 *  - A request allocation policy: for selecting a request to allocate to a vehicle.
 *
 * @author gphhucarp, William Huang
 */
public abstract class DecisionProcess {
    protected DecisionProcessState state;
    protected PriorityQueue<DecisionProcessEvent> eventQueue;
    protected List<Request> waitingList;
    protected VehiclePolicy vehiclePolicy;
    protected RequestPolicy requestPolicy;
    protected List<Double> decisionTimes = new ArrayList<Double>();

    public DecisionProcess(DecisionProcessState state,
                           PriorityQueue<DecisionProcessEvent> eventQueue,
                           List<Request> waitingList,
                           VehiclePolicy vehiclePolicy,
                           RequestPolicy requestPolicy) {
        this.state = state;
        this.eventQueue = eventQueue;
        this.waitingList = waitingList;
        this.vehiclePolicy = vehiclePolicy;
        this.requestPolicy = requestPolicy;
        state.setWaitingList(waitingList);
    }

    // Getters
    public DecisionProcessState getState() {
        return state;
    }
    public PriorityQueue<DecisionProcessEvent> getEventQueue() {
        return eventQueue;
    }
    public List<Request> getWaitingList() { return waitingList; }
    public VehiclePolicy getVehiclePolicy() {
        return vehiclePolicy;
    }
    public RequestPolicy getRequestPolicy() { return requestPolicy; }
    public List<Double> getDecisionTimes() { return decisionTimes; }

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
    public void addWaiting(Request request) { waitingList.add(request); }

    /**
     * Remove a request from the waiting list.
     *
     * @param request the request to be removed.
     */
    public void removeWaiting(Request request) { waitingList.remove(request); }

    public void addDecisionTime(Double d) { decisionTimes.add(d); }

    /**
     * Initialise a reactive decision process from an instance,
     * a vehicle allocation policy, and a request allocation policy.
     *
     * @param instance the given instance.
     * @param vehiclePolicy the vehicle allocation policy.
     * @param requestPolicy the request allocation policy.
     * @return the initial reactive decision process.
     */
    public static ReactiveDecisionProcess initReactive(Instance instance,
                                                       VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy) {
        DecisionProcessState state = new DecisionProcessState(instance);
        PriorityQueue<DecisionProcessEvent> eventQueue = new PriorityQueue<>();
        for (Request request : instance.getRequests()) {
            eventQueue.add(new ReactiveRequestEvent(request.getTRec(), request));
        }
        List<Request> waitingList = new ArrayList<>();
        return new ReactiveDecisionProcess(state, eventQueue, waitingList, vehiclePolicy, requestPolicy);
    }

    /**
     * Run the decision process.
     */
    public void run() {
        while (!eventQueue.isEmpty()) {
            DecisionProcessEvent event = eventQueue.poll();
            // System.out.println(event);
            state.setTime(event.getTime());
            event.trigger(this);
            state.updateSolution();
        }
        if (waitingList.isEmpty()) {
            state.getSolution().setFeasible(true);
        }
        state.getSolution().setNumRequests(state.getInstance().getNumRequests());
        state.getSolution().setName(state.getInstance().getName());
        double avgDecisionTime;
        if (decisionTimes.isEmpty()) {
            avgDecisionTime = 0;
        }
        else {
            avgDecisionTime = decisionTimes.stream()
                    .mapToDouble(Double::doubleValue)
                    .sum() / decisionTimes.size();
        }
        state.getSolution().setAvgDecisionTime(avgDecisionTime);
        state.getSolution().setDp(this);
    }

    /**
     * Reset the decision process.
     * This is done by resetting the decision process state and event queue.
     */
    public abstract void reset();
}
