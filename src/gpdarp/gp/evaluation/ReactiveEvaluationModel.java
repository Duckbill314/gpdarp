package gpdarp.gp.evaluation;

import ec.EvolutionState;
import ec.Fitness;
import ec.gp.koza.KozaFitness;
import gpdarp.core.Instance;
import gpdarp.core.Objective;
import gpdarp.core.Request;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;
import gpdarp.representation.Solution;
import gpdarp.representation.route.Route;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * A reactive evaluation model is a set of reactive decision processes, corresponding to a set of instances.
 * It evaluates a pair of policies by applying them on each decision process,
 * and returning the average objective values across the processes.
 * Evaluation can happen batchwise, rotating through all instances.
 * For the test set, there is the option to return the normalised or raw objective values.
 * Training and validation are always normalised.
 * Normalisation divides the objective value by the reference objective value.
 *
 * @author gphhucarp, William Huang
 */
public class ReactiveEvaluationModel extends EvaluationModel {
    public static double FEASIBLE_THRESHOLD = 1000000;

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

        Pair<List<Solution>, Double> solutionPair = evaluateFitnesses(vehiclePolicy, requestPolicy, normalise);
        List<Solution> solutions = solutionPair.getLeft();
        Double fitnessValue = solutionPair.getRight();

        KozaFitness f = (KozaFitness) fitness;
        f.setStandardizedFitness(state, fitnessValue);

        return solutions;
    }

    /**
     * Helper method for evaluating the average fitness for a pair of policies across a set of instances.
     *
     * @param vehiclePolicy the vehicle allocation policy.
     * @param requestPolicy the request allocation policy.
     * @param normalise whether to apply normalisation.
     * @return a list of all the generated solutions and their corresponding average fitness.
     */
    public Pair<List<Solution>, Double> evaluateFitnesses(VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy,
                                                          boolean normalise) {

        boolean feasible = true;
        double fitness = 0;
        List<Solution> solutions = new ArrayList<>();

        if (isRotating()) {
            for (int i = rotationIndex; i < rotationIndex + batchsize; i++) {
                double fitnessValue = getFitnessValue(vehiclePolicy, requestPolicy, solutions, i, normalise);
                if (fitnessValue >= 0 || fitnessValue < FEASIBLE_THRESHOLD) {
                    fitness += fitnessValue;
                }
                else {
                    feasible = false;
                }
            }
            fitness /= batchsize;
        }
        else {
            for (int i = 0; i < instanceSamples.size(); i++) {
                double fitnessValue = getFitnessValue(vehiclePolicy, requestPolicy, solutions, i, normalise);
                if (fitnessValue >= 0 || fitnessValue < FEASIBLE_THRESHOLD) {
                    fitness += fitnessValue;
                }
                else {
                    feasible = false;
                }
            }
            fitness /= instanceSamples.size();
        }

        if (!feasible) {
            fitness = Double.MAX_VALUE;
        }

        return Pair.of(solutions, fitness);
    }

    /**
     * Helper method for evaluating a single solution fitness.
     *
     * @param vehiclePolicy the vehicle allocation policy.
     * @param requestPolicy the request allocation policy.
     * @param solutions the list for storing the obtained solution.
     * @param i the index of the instance sample to evaluate.
     * @param normalise whether to apply normalisation.
     * @return the obtained singular fitness value.
     */
    private double getFitnessValue(VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy,
                                   List<Solution> solutions, int i, boolean normalise) {

        Instance sample = instanceSamples.get(i);
        ReactiveDecisionProcess dp = DecisionProcess.initReactive(sample.clone(), vehiclePolicy, requestPolicy);
        dp.run();
        Solution solution = dp.getState().getSolution();
        solutions.add(solution);

        Objective objective = objectives.getFirst();
        double objValue = solution.objValue(objective);

        assert (objValue >= 0);

        if (objValue >= FEASIBLE_THRESHOLD) {
            return Double.MAX_VALUE;
        }

        if (normalise) {
            double refValue = getObjRefValue(i, objective);
            return objValue / refValue;
        }

        return objValue;
    }

    /**
     * Helper method for running the validation process.
     * This is only accessed during reading of a GPResult from file.
     *
     * @param vehiclePolicy the vehicle allocation policy.
     * @param requestPolicy the request allocation policy.
     * @return the average fitness of the individual against the validation set.
     */
    public double validation(VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy) {
        assert (validating);

        double fitness = 0;

        for (int i = 0; i < validationSamples.size(); i++) {
            Instance sample = validationSamples.get(i);
            ReactiveDecisionProcess dp = DecisionProcess.initReactive(sample.clone(), vehiclePolicy, requestPolicy);
            dp.run();
            Solution solution = dp.getState().getSolution();

            Objective objective = objectives.getFirst();
            double objValue = solution.objValue(objective);
            double refValue = super.getValObjRefValue(i, objective);

            assert (objValue >= 0);

            if (objValue < FEASIBLE_THRESHOLD) {
                fitness += objValue / refValue;
            } else {
                return Double.MAX_VALUE;
            }
        }
        fitness /= validationSamples.size();
        return fitness;
    }
}
