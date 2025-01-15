package gpdarp.decisionprocess.reactive;

import gpdarp.core.Request;
import gpdarp.decisionprocess.*;
import gpdarp.decisionprocess.reactive.event.ReactiveRequestEvent;

import java.util.List;
import java.util.PriorityQueue;

/**
 * The reactive decision process builds the solution in real time.
 * It assigns requests/actions to vehicles based on the supplied policies.
 *
 * @author gphhucarp, William Huang
 */
public class ReactiveDecisionProcess extends DecisionProcess {

    public ReactiveDecisionProcess(DecisionProcessState state,
                                   PriorityQueue<DecisionProcessEvent> eventQueue,
                                   List<Request> waitingList,
                                   VehiclePolicy vehiclePolicy,
                                   RequestPolicy requestPolicy) {
        super(state, eventQueue, waitingList, vehiclePolicy, requestPolicy);
    }

    @Override
    public void reset() {
        state.reset();
        eventQueue.clear();
        for (Request request : state.getInstance().getRequests()) {
            eventQueue.add(new ReactiveRequestEvent(request.getTRec(), request));
        }
        waitingList.clear();
    }
}
