package gpdarp.decisionprocess.reactive;

import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.DecisionSituation;
import gpdarp.representation.route.Route;

import java.util.LinkedList;
import java.util.List;

/**
 * The decision situation for reactive decision process.
 */

public class ReactiveDecisionSituation extends DecisionSituation {

    private List<Vehicle> pool;
    private DecisionProcessState state;

    public ReactiveDecisionSituation(List<Vehicle> pool, DecisionProcessState state) {
        this.pool = pool;
        this.state = state;
    }

    // Getters
    public List<Vehicle> getPool() {
        return pool;
    }
    public DecisionProcessState getState() {
        return state;
    }

    @Override
    public ReactiveDecisionSituation clone() {
        List<Vehicle> clonedPool = Vehicle.listClone(pool);
        DecisionProcessState clonedState = state.clone();

        return new ReactiveDecisionSituation(clonedPool, clonedState);
    }
}
