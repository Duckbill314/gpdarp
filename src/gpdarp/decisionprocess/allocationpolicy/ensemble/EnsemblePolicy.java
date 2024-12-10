package gpdarp.decisionprocess.allocationpolicy.ensemble;

import gpdarp.core.Arc;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.decisionprocess.TieBreaker;
import gpdarp.decisionprocess.reactive.ReactiveDecisionSituation;

import java.util.List;

/**
 * An ensemble of allocation policies.
 *
 * @author gphhucarp, William Huang
 */
public class EnsemblePolicy extends AllocationPolicy {

    private AllocationPolicy[] policies; // the element policies in the ensemble
    private double[] weights; // the weights for the element policies
    private Combiner combiner; // the combiner

    public EnsemblePolicy(PoolFilter poolFilter, TieBreaker tieBreaker, AllocationPolicy[] policies, double[] weights, Combiner combiner) {
        super(poolFilter, tieBreaker);
        this.policies = policies;
        this.weights = weights;
        this.combiner = combiner;
    }

    public EnsemblePolicy(PoolFilter poolFilter, AllocationPolicy[] policies, double[] weights, Combiner combiner) {
        super(poolFilter);
        this.policies = policies;
        this.weights = weights;
        this.combiner = combiner;
    }

    public EnsemblePolicy(TieBreaker tieBreaker, AllocationPolicy[] policies, double[] weights, Combiner combiner) {
        super(tieBreaker);
        this.policies = policies;
        this.weights = weights;
        this.combiner = combiner;
    }

    public EnsemblePolicy(PoolFilter poolFilter, TieBreaker tieBreaker, AllocationPolicy[] policies, Combiner combiner) {
        super(poolFilter, tieBreaker);
        this.policies = policies;
        this.combiner = combiner;
        weights = new double[policies.length];
        for (int i = 0; i < weights.length; i++)
            weights[i] = 1;
    }

    public EnsemblePolicy(PoolFilter poolFilter, AllocationPolicy[] policies, Combiner combiner) {
        super(poolFilter);
        this.policies = policies;
        this.combiner = combiner;
        weights = new double[policies.length];
        for (int i = 0; i < weights.length; i++)
            weights[i] = 1;
    }

    public EnsemblePolicy(TieBreaker tieBreaker, AllocationPolicy[] policies, Combiner combiner) {
        super(tieBreaker);
        this.policies = policies;
        this.combiner = combiner;
        weights = new double[policies.length];
        for (int i = 0; i < weights.length; i++)
            weights[i] = 1;
    }

    // Getters
    public AllocationPolicy[] getPolicies() { return policies; }
    public AllocationPolicy getPolicy(int index) { return policies[index]; }
    public double[] getWeights() { return weights; }
    public double getWeight(int index) { return weights[index]; }
    public Combiner getCombiner() { return combiner; }
    public int size() { return policies.length; }

    // Setters
    public void setPolicies(AllocationPolicy[] policies) { this.policies = policies; }
    public void setPolicy(int index, AllocationPolicy policy) { policies[index] = policy; }
    public void setCombiner(Combiner combiner) { this.combiner = combiner; }


    @Override
    public Vehicle next(ReactiveDecisionSituation rds, Request request) {
        List<Vehicle> pool = rds.getPool();
        DecisionProcessState state = rds.getState();

        List<Vehicle> filteredPool = poolFilter.filter(pool, request, state);

        if (filteredPool.isEmpty())
            return null;

        return combiner.next(pool, request, state, this);
    }

    @Override
    public double priority(Vehicle candidate, Request request, DecisionProcessState state) { return 0; }
}
