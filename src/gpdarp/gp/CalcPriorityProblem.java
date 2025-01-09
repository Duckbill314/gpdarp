package gpdarp.gp;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.representation.route.Route;

/**
 * The problem for calculating the priority of a candidate vehicle or request.
 *
 * @author Yi Mei, William Huang
 */
public class CalcPriorityProblem extends Problem implements SimpleProblemForm {

    private final Vehicle vehicle;
    private final Request request;
    private final Route route;
    private final DecisionProcessState state;

    public CalcPriorityProblem(Vehicle vehicle, Request request, Route route, DecisionProcessState state) {
        this.vehicle = vehicle;
        this.request = request;
        this.route = route;
        this.state = state;
    }

    // Getters
    public Vehicle getVehicle() { return vehicle; }
    public Request getRequest() { return request; }
    public Route getRoute() { return route; }
    public DecisionProcessState getState() { return state; }

    @Override
    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) { }
}
