package gpdarp.gp;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;

/**
 * The problem for calculating the priority of a candidate vehicle or request.
 *
 * @author Yi Mei, William Huang
 */
public class CalcPriorityProblem extends Problem implements SimpleProblemForm {

    private Vehicle vehicle;
    private Request request;
    private DecisionProcessState state;

    public CalcPriorityProblem(Vehicle vehicle, Request request, DecisionProcessState state) {
        this.vehicle = vehicle;
        this.request = request;
        this.state = state;
    }

    // Getters
    public Vehicle getVehicle() { return vehicle; }
    public Request getRequest() { return request; }
    public DecisionProcessState getState() { return state; }

    @Override
    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) { }
}
