package gpdarp.decisionprocess.allocationpolicy.ensemble;

import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.representation.VehiclePool;
import gpdarp.representation.route.Route;

import java.util.Map;

/**
 * An ensemble of allocation policies.
 *
 * @author gphhucarp, William Huang
 */
public class EnsemblePolicy extends VehiclePolicy {
    private VehiclePolicy[] policies; // the element policies in the ensemble
    private double[] weights; // the weights for the element policies
    private Combiner combiner; // the combiner

    public EnsemblePolicy(PoolFilter poolFilter, TieBreaker tieBreaker, VehiclePolicy[] policies, double[] weights, Combiner combiner) {
        super(poolFilter, tieBreaker);
        this.policies = policies;
        this.weights = weights;
        this.combiner = combiner;
    }

    // Getters
    public VehiclePolicy[] getPolicies() { return policies; }
    public VehiclePolicy getPolicy(int index) { return policies[index]; }
    public double[] getWeights() { return weights; }
    public double getWeight(int index) { return weights[index]; }
    public Combiner getCombiner() { return combiner; }
    public int size() { return policies.length; }

    // Setters
    public void setPolicies(VehiclePolicy[] policies) { this.policies = policies; }
    public void setPolicy(int index, VehiclePolicy policy) { policies[index] = policy; }
    public void setCombiner(Combiner combiner) { this.combiner = combiner; }


    @Override
    public Map.Entry<Vehicle, Route> next(DecisionProcessState state, Request request) {
        VehiclePool filteredVehiclePool = poolFilter.filterVehicles(state, request);
        state.setPool(filteredVehiclePool);

        if (filteredVehiclePool.isEmpty())
            return null;

        return combiner.next(state, request, this);
    }

    @Override
    public double priority(Map.Entry<Vehicle, Route> candidate, DecisionProcessState state, Request request) {
        return 0;
    }
}
