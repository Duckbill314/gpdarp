package gpdarp.gp.evaluation;

import ec.EvolutionState;
import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import gpdarp.core.Instance;
import gpdarp.core.Objective;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;
import gpdarp.representation.Solution;

/**
 * A reactive evaluation model is a set of reactive decision processes, corresponding to a set of instances.
 * It evaluates a reactive routing policy by applying the policy on each decision process,
 * and returning the average normalised objective values across the processes.
 *
 * It includes
 *  - A list of instances,
 *  - The reference objective value map, indicating the reference value
 *    of a given reactive decision process and a given objective.
 *
 * @author gphhucarp, William Huang
 */
public class ReactiveEvaluationModel extends EvaluationModel {
    @Override
    public void evaluate(AllocationPolicy policy, Fitness fitness, EvolutionState state) {
        double[] fitnesses = evaluateFitnesses(policy, fitness, state);

        for (int j = 0; j < fitnesses.length; j++) {
            fitnesses[j] /= instanceSamples.size();
        }

        MultiObjectiveFitness f = (MultiObjectiveFitness) fitness;
        f.setObjectives(state, fitnesses);
    }

    @Override
    public void evaluateOriginal(AllocationPolicy policy, Fitness fitness, EvolutionState state) {
        double[] fitnesses = evaluateFitnesses(policy, fitness, state);

        MultiObjectiveFitness f = (MultiObjectiveFitness) fitness;
        f.setObjectives(state, fitnesses);
    }

    public double[] evaluateFitnesses(AllocationPolicy policy, Fitness fitness, EvolutionState state) {
        double[] fitnesses = new double[objectives.size()];

        for (Instance sample : instanceSamples) {
            ReactiveDecisionProcess dp = DecisionProcess.initReactive(sample.clone(), policy);
            dp.run();
            Solution solution = dp.getState().getSolution();

            for (int j = 0; j < fitnesses.length; j++) {
                Objective objective = objectives.get(j);
                double objValue = solution.objValue(objective);
                fitnesses[j] += objValue;
            }
        }
        return fitnesses;
    }
}
