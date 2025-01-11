package gpdarp.gp.evaluation;

import ec.EvolutionState;
import ec.Fitness;
import ec.gp.koza.KozaFitness;
import ec.multiobjective.MultiObjectiveFitness;
import gpdarp.core.Instance;
import gpdarp.core.Objective;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;
import gpdarp.representation.Solution;

import java.sql.Array;

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
    public void evaluate(VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy,
                         Fitness fitness, EvolutionState state) {

        double fitnessValue = evaluateFitnesses(vehiclePolicy, requestPolicy);
        fitnessValue /= instanceSamples.size();

        KozaFitness f = (KozaFitness) fitness;
        f.setStandardizedFitness(state, fitnessValue);
    }

    @Override
    public void evaluateOriginal(VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy,
                                 Fitness fitness, EvolutionState state) {

        double fitnessValue = evaluateFitnesses(vehiclePolicy, requestPolicy);

        KozaFitness f = (KozaFitness) fitness;
        f.setStandardizedFitness(state, fitnessValue);
    }

    public double evaluateFitnesses(VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy) {
        double fitnessValue = 0;

        for (Instance sample : instanceSamples) {
            ReactiveDecisionProcess dp = DecisionProcess.initReactive(sample.clone(), vehiclePolicy, requestPolicy);
            dp.run();
            Solution solution = dp.getState().getSolution();

                Objective objective = objectives.getFirst();
                double objValue = solution.objValue(objective);
                fitnessValue += objValue;
        }
        return fitnessValue;
    }
}
