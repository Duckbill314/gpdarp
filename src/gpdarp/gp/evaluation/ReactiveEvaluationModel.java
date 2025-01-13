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
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

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

        double fitnessValue = evaluateFitnesses(vehiclePolicy, requestPolicy, true).getRight();

        KozaFitness f = (KozaFitness) fitness;
        f.setStandardizedFitness(state, fitnessValue);
    }

    @Override
    public List<Solution> evaluateOriginal(VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy,
                                 Fitness fitness, EvolutionState state) {

        Pair<List<Solution>, Double> solutionPair = evaluateFitnesses(vehiclePolicy, requestPolicy, false);
        List<Solution> solutions = solutionPair.getLeft();
        Double fitnessValue = solutionPair.getRight();

        KozaFitness f = (KozaFitness) fitness;
        f.setStandardizedFitness(state, fitnessValue);

        return solutions;
    }

    public Pair<List<Solution>, Double> evaluateFitnesses(VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy,
                                                          boolean normalise) {

        double fitnessValue = 0;
        List<Solution> solutions = new ArrayList<>();

        if (isRotating()) {
            for (int i = rotationIndex; i < rotationIndex + batchsize; i++) {
                fitnessValue = getFitnessValue(vehiclePolicy, requestPolicy, fitnessValue, solutions, i, normalise);
                fitnessValue /= batchsize;
            }
        }
        else {
            for (int i = 0; i < instanceSamples.size(); i++) {
                fitnessValue = getFitnessValue(vehiclePolicy, requestPolicy, fitnessValue, solutions, i, normalise);
                fitnessValue /= instanceSamples.size();
            }
        }

        return Pair.of(solutions, fitnessValue);
    }

    private double getFitnessValue(VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy,
                                   double fitnessValue, List<Solution> solutions, int i, boolean normalise) {

        Instance sample = instanceSamples.get(i);
        ReactiveDecisionProcess dp = DecisionProcess.initReactive(sample.clone(), vehiclePolicy, requestPolicy);
        dp.run();
        Solution solution = dp.getState().getSolution();
        solutions.add(solution);

        Objective objective = objectives.getFirst();
        double objValue = solution.objValue(objective);

        if (normalise) {
            double refValue = getObjRefValue(i, objective);
            fitnessValue += objValue / refValue;
        }

        return fitnessValue;
    }
}
