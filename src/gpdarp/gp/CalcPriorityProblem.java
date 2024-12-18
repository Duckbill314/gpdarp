package gpdarp.gp;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import gpdarp.core.Allocatable;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.representation.route.Route;

import java.util.Map;

/**
 * The problem for calculating the priority of a candidate vehicle or request.
 *
 * @author Yi Mei, William Huang
 */
public class CalcPriorityProblem<T1 extends Allocatable, T2 extends Allocatable>
        extends Problem implements SimpleProblemForm {

    private T1 candidate;
    private DecisionProcessState state;
    private T2 control;

    public CalcPriorityProblem(T1 candidate, DecisionProcessState state, T2 control) {
        this.candidate = candidate;
        this.state = state;
        this.control = control;
    }

    // Getters
    public T1 getCandidate() { return candidate; }
    public DecisionProcessState getState() { return state; }
    public T2 getControl() { return control; }

    @Override
    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) { }
}
