package gpdarp.gp.terminal.feature;

import gpdarp.core.*;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.representation.route.Route;

/**
 * Returns the expected penalty of serving the request.
 *
 * @author William Huang
 */
public class ExpectedPenalty extends FeatureGPNode {
    public ExpectedPenalty() {
        super();
        name = "PEN";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Vehicle vehicle = calcPriorityProblem.getVehicle();
        Request request = calcPriorityProblem.getRequest();
        DecisionProcessState state = calcPriorityProblem.getState();
        Instance instance = state.getInstance();
        Node currPos = vehicle.getCurrPos();

        if (request.getType() == Request.RequestType.CHARGE) {
            return 0;
        }

        Route route = new Route();
        Node pickup = request.getPickup();
        Node dropoff = request.getDropoff();

        Arc toPickup = new Arc(currPos, pickup);
        toPickup.updateEtas(instance, vehicle);
        route.push(toPickup);

        Arc toDropoff = new Arc(pickup, dropoff);
        toDropoff.updateEtas(instance, vehicle);
        route.push(toDropoff);

        return instance.getLatenessPenalty() * route.calculatePenalty();
    }
}
