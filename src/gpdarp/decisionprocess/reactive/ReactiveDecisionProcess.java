package gpdarp.decisionprocess.reactive;

import gpdarp.core.WaitingRequest;
import gpdarp.core.Request;
import gpdarp.decisionprocess.*;

import java.util.List;
import java.util.PriorityQueue;

/**
 * The reactive decision process builds the solution in real time.
 * It assigns requests to vehicles based on whichever one can fulfill them at minimum cost.
 * It also plans refills whenever necessary.
 *
 * @author gphhucarp, William Huang
 */
public class ReactiveDecisionProcess extends DecisionProcess {

    public ReactiveDecisionProcess(DecisionProcessState state,
                                   PriorityQueue<DecisionProcessEvent> eventQueue,
                                   List<WaitingRequest> waitingList,
                                   VehiclePolicy vehiclePolicy,
                                   RequestPolicy requestPolicy) {
        super(state, eventQueue, waitingList, vehiclePolicy, requestPolicy);
    }

    @Override
    public void reset() {
        state.reset();
        eventQueue.clear();
    }

    @Override
    protected ReactiveDecisionProcess clone() {
        DecisionProcessState clonedState = state.clone();
        PriorityQueue<DecisionProcessEvent> clonedEQ = new PriorityQueue<>();
        clonedEQ.addAll(eventQueue);

        return new ReactiveDecisionProcess(clonedState, clonedEQ, Request.listClone(waitingList),
                vehiclePolicy, requestPolicy);
    }
}
