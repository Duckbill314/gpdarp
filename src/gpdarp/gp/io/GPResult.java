package gpdarp.gp.io;

import ec.Fitness;
import ec.Problem;
import ec.gp.koza.KozaFitness;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.allocationpolicy.requestpolicy.GPRequestPolicy;
import gpdarp.decisionprocess.allocationpolicy.vehiclepolicy.GPVehiclePolicy;
import gpdarp.gp.ReactiveGPHHProblem;
import gpdarp.gp.UCARPPrimitiveSet;
import gpdarp.gp.evaluation.ReactiveEvaluationModel;
import gputils.LispUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A GP result is a class that stores the information read from an out.stat file produced by a GP run.
 * It includes:
 *  - A list of pairs of expressions representing the evolved trees, one pair per generation.
 *  - A list of solutions, each comprised of a paired VehiclePolicy and RequestPolicy.
 *  - A list of training fitnesses, each for a solution.
 *  - A list of test fitnesses, each for a solution.
 *  - The aforementioned information for the best individual of the run (i.e. the individual with the best validation).
 *  - The time statistics, i.e. the time spent for each generation.
 *  - A list of the validation fitnesses for all the best individuals per generation.
 *  - The average decision time of the simulation during testing.
 *
 * @author gphhucarp, William Huang
 */

public class GPResult {
    public static double FEASIBLE_THRESHOLD = 1000000;

    private List<Pair<String, String>> expressions;
    private List<Pair<VehiclePolicy, RequestPolicy>> solutions;
    private List<Fitness> trainFitnesses;
    private List<Fitness> testFitnesses;
    private DescriptiveStatistics timeStat;
    private int bestIndex;
    private double avgDecisionTime;
    private List<Double> validations;

    public GPResult() {
        expressions = new ArrayList<>();
        solutions = new ArrayList<>();
        trainFitnesses = new ArrayList<>();
        testFitnesses = new ArrayList<>();
        validations = new ArrayList<>();
    }

    // Getters
    public Pair<String, String> getBestExpression() { return getExpressionAtGen(bestIndex); }
    public List<Pair<VehiclePolicy, RequestPolicy>> getSolutions() { return solutions; }
    public Pair<VehiclePolicy, RequestPolicy> getBestSolution() { return getSolutionAtGen(bestIndex); }
    public Fitness getBestTrainFitness() { return getTrainFitnessAtGen(bestIndex); }
    public Fitness getBestTestFitness() { return getTestFitnessAtGen(bestIndex); }
    public Pair<String, String> getExpressionAtGen(int gen) { return expressions.get(gen); }
    public Pair<VehiclePolicy, RequestPolicy> getSolutionAtGen(int gen) { return solutions.get(gen); }
    public Fitness getTrainFitnessAtGen(int gen) { return trainFitnesses.get(gen); }
    public Fitness getTestFitnessAtGen(int gen) { return testFitnesses.get(gen); }
    public double getTimeAtGen(int gen) { return timeStat.getElement(gen); }
    public int getBestIndex() { return bestIndex; }
    public double getAvgDecisionTime() { return avgDecisionTime; }
    public double getValidationAtGen(int gen) { return validations.get(gen); }

    // Setters
    public void setTimeStat(DescriptiveStatistics timeStat) { this.timeStat = timeStat; }
    public void setBestIndex(int bestIndex) { this.bestIndex = bestIndex; }
    public void setAvgDecisionTime(double avgDecisionTime) { this.avgDecisionTime = avgDecisionTime; }

    // Adders
    public void addExpression(Pair<String, String> expression) { expressions.add(expression); }
    public void addSolution(Pair<VehiclePolicy, RequestPolicy> solution) { solutions.add(solution); }
    public void addTrainFitness(Fitness fitness) { trainFitnesses.add(fitness); }
    public void addTestFitness(Fitness fitness) { testFitnesses.add(fitness); }
    public void addValidation(Double validation) { validations.add(validation); }

    public static GPResult readFromFile(File file,
                                        Problem problem,
                                        SolutionType solutionType,
                                        FitnessType fitnessType) {

        if (Objects.requireNonNull(solutionType) == SolutionType.SIMPLE_SOLUTION) {
            return readSimpleSolutionFromFile(file, problem, fitnessType);
        }
        return null;
    }

    public static GPResult readSimpleSolutionFromFile(File file,
                                                      Problem problem,
                                                      FitnessType fitnessType) {

        GPResult result = new GPResult();

        String line;
        Fitness fitness;
        double val;
        double bestVal = Double.MAX_VALUE;
        String expression1;
        String expression2;
        Pair<String, String> expression;
        Pair<VehiclePolicy, RequestPolicy> solution;
        int infeasibleCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (!(line = br.readLine()).equals("Best Individual of Run:")) {
                if (line.startsWith("Generation")) {
                    br.readLine();
                    br.readLine();
                    br.readLine();
                    line = br.readLine();
                    fitness = readFitnessFromLine(line, fitnessType);
                    br.readLine();
                    expression1 = br.readLine();
                    expression1 = LispUtils.simplifyExpression(expression1);
                    br.readLine();
                    expression2 = br.readLine();
                    expression2 = LispUtils.simplifyExpression(expression2);

                    expression = Pair.of(expression1, expression2);
                    result.addExpression(expression);

                    VehiclePolicy vehiclePolicy = new GPVehiclePolicy(
                            LispUtils.parseExpression(expression1, UCARPPrimitiveSet.primitiveSet()));

                    RequestPolicy requestPolicy = new GPRequestPolicy(
                            LispUtils.parseExpression(expression2, UCARPPrimitiveSet.primitiveSet()));

                    solution = Pair.of(vehiclePolicy, requestPolicy);
                    result.addSolution(solution);

                    result.addTrainFitness(fitness);
                    result.addTestFitness((Fitness)fitness.clone());

                    ReactiveEvaluationModel model = (ReactiveEvaluationModel) ((ReactiveGPHHProblem)problem)
                            .getEvaluationModel();

                    val = model.validation(vehiclePolicy, requestPolicy);
                    result.addValidation(val);

                    if (val < 0 || val >= FEASIBLE_THRESHOLD) {
                        infeasibleCount++;
                    }
                    else {
                        if (val < bestVal) {
                            bestVal = val;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Number of invalid individuals: "+ infeasibleCount);

        // Identify and set the best solution
        int bestIndex = result.validations.indexOf(bestVal);
        result.setBestIndex(bestIndex);

        return result;
    }

    private static Fitness readFitnessFromLine(String line,
                                               FitnessType fitnessType) {

        if (Objects.requireNonNull(fitnessType) == FitnessType.SIMPLE_FITNESS) {
            return readSimpleFitnessFromLine(line);
        }
        return null;
    }

    private static Fitness readSimpleFitnessFromLine(String line) {
        String[] segments = line.split(" ");
        String standardisedFitness = segments[1];
        standardisedFitness = standardisedFitness.replace("Standardized=", "");
        double fitness = Double.parseDouble(standardisedFitness);
        KozaFitness f = new KozaFitness();
        f.setFitnessExplicitly(fitness);

        return f;
    }

    public static DescriptiveStatistics readTimeFromFile(File file) {
        DescriptiveStatistics generationalTimeStat = new DescriptiveStatistics();

        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while(true) {
                line = br.readLine();

                if (line == null)
                    break;

                String[] commaSegments = line.split(",");
                generationalTimeStat.addValue(Double.parseDouble(commaSegments[1]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return generationalTimeStat;
    }
}
