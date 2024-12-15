package gpdarp.gp;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.representation.route.Route;

import java.util.Map;

/**
 * The problem for calculating the priority of a candidate task.
 *
 * @author Yi Mei
 */
public class CalcPriorityProblem extends Problem implements SimpleProblemForm {

    private Map.Entry<Vehicle, Route> candidate;
    private Request request;
    private DecisionProcessState state;

    public CalcPriorityProblem(Map.Entry<Vehicle, Route> candidate,
                               Request request,
                               DecisionProcessState state) {
        this.candidate = candidate;
        this.request = request;
        this.state = state;
    }

    public Map.Entry<Vehicle, Route> getCandidate() {
        return candidate;
    }
    public Request getRequest() {
        return request;
    }
    public DecisionProcessState getState() {
        return state;
    }

    @Override
    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) { }
}
