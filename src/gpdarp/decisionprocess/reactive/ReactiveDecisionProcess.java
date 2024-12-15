package gpdarp.decisionprocess.reactive;

import gpdarp.core.Request;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessEvent;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.AllocationPolicy;

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
                                   List<Request> waitingList,
                                   AllocationPolicy allocationPolicy) {
        super(state, eventQueue, waitingList, allocationPolicy);
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

        return new ReactiveDecisionProcess(clonedState, clonedEQ, allocationPolicy);
    }
}
