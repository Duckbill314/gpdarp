package gpdarp.gp.io;

import ec.Fitness;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import gpdarp.decisionprocess.AllocationPolicy;
import gpdarp.decisionprocess.allocationpolicy.GPAllocationPolicy;
import gpdarp.decisionprocess.allocationpolicy.ensemble.EnsemblePolicy;
import gpdarp.gp.UCARPPrimitiveSet;
import gpdarp.gp.ReactiveGPHHProblem;
import gputils.LispUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A GP result is a class that stores the information read from an out.stat file produced by a GP run.
 * It includes
 *  - a list of solutions (best individuals), one per generation.
 *  - a list of training fitnesses, each for a solution.
 *  - a list of demo fitnesses, each for a solution.
 *  - a best solution according to the training fitness.
 *  - the training fitness of the best solution.
 *  - the demo fitness of the best solution.
 *  - the time statistics, i.e. the time spent for each generation.
 */

public class GPResult {
    private List<String> expressions;
    private List<AllocationPolicy> solutions;
    private List<Fitness> trainFitnesses;
    private List<Fitness> testFitnesses;
    private String bestExpression;
    private AllocationPolicy bestSolution;
    private Fitness bestTrainFitness;
    private Fitness bestTestFitness;
    private DescriptiveStatistics timeStat;

    public GPResult() {
        expressions = new ArrayList<>();
        solutions = new ArrayList<>();
        trainFitnesses = new ArrayList<>();
        testFitnesses = new ArrayList<>();
    }

    public List<String> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<String> expressions) {
        this.expressions = expressions;
    }

    public String getBestExpression() {
        return bestExpression;
    }

    public void setBestExpression(String bestExpression) {
        this.bestExpression = bestExpression;
    }

    public List<AllocationPolicy> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<AllocationPolicy> solutions) {
        this.solutions = solutions;
    }

    public List<Fitness> getTrainFitnesses() {
        return trainFitnesses;
    }

    public void setTrainFitnesses(List<Fitness> trainFitnesses) {
        this.trainFitnesses = trainFitnesses;
    }

    public List<Fitness> getTestFitnesses() {
        return testFitnesses;
    }

    public void setTestFitnesses(List<Fitness> testFitnesses) {
        this.testFitnesses = testFitnesses;
    }

    public AllocationPolicy getBestSolution() {
        return bestSolution;
    }

    public void setBestSolution(AllocationPolicy bestSolution) {
        this.bestSolution = bestSolution;
    }

    public Fitness getBestTrainFitness() {
        return bestTrainFitness;
    }

    public void setBestTrainFitness(Fitness bestTrainFitness) {
        this.bestTrainFitness = bestTrainFitness;
    }

    public Fitness getBestTestFitness() {
        return bestTestFitness;
    }

    public void setBestTestFitness(Fitness bestTestFitness) {
        this.bestTestFitness = bestTestFitness;
    }

    public DescriptiveStatistics getTimeStat() {
        return timeStat;
    }

    public void setTimeStat(DescriptiveStatistics timeStat) {
        this.timeStat = timeStat;
    }

    public AllocationPolicy getSolutionAtGen(int gen) {
        return solutions.get(gen);
    }

    public Fitness getTrainFitnessAtGen(int gen) {
        return trainFitnesses.get(gen);
    }

    public Fitness getTestFitnessAtGen(int gen) {
        return testFitnesses.get(gen);
    }

    public double getTimeAtGen(int gen) {
        return timeStat.getElement(gen);
    }

    public void addExpression(String expression) {
        expressions.add(expression);
    }

    public void addSolution(AllocationPolicy solution) {
        solutions.add(solution);
    }

    public void addTrainFitness(Fitness fitness) {
        trainFitnesses.add(fitness);
    }

    public void addTestFitness(Fitness fitness) {
        testFitnesses.add(fitness);
    }

    public static GPResult readFromFile(File file,
                                        Problem problem,
                                        SolutionType solutionType,
                                        FitnessType fitnessType) {
        switch (solutionType) {
            case SIMPLE_SOLUTION:
                return readSimpleSolutionFromFile(file, problem, fitnessType);
            case CC_SOLUTION:
                return readCCSolutionFromFile(file, problem, fitnessType);
            case RF_SOLUTION:
                return readRFSolutionFromFile(file, problem, fitnessType);
            default:
                return null;
        }
    }

    public static GPResult readSimpleSolutionFromFile(File file,
                                                      Problem problem,
                                                      FitnessType fitnessType) {
        ReactiveGPHHProblem prob = (ReactiveGPHHProblem)problem;

        GPResult result = new GPResult();

        String line;
        Fitness fitness = null;
        AllocationPolicy solution = null;
        String expression = "";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (!(line = br.readLine()).equals("Best Individual of Run:")) {
                if (line.startsWith("Generation")) {
                    br.readLine();
                    br.readLine();
                    br.readLine();
                    line = br.readLine();
                    fitness = readFitnessFromLine(line, fitnessType);
                    br.readLine();
                    expression = br.readLine();

                    expression = LispUtils.simplifyExpression(expression);

                    result.addExpression(expression);

                    AllocationPolicy allocationPolicy =
                            new GPAllocationPolicy(prob.getPoolFilter(),
                                    LispUtils.parseExpression(expression,
                                            UCARPPrimitiveSet.wholePrimitiveSet()));

                    result.addSolution(allocationPolicy);
                    result.addTrainFitness(fitness);
                    result.addTestFitness((Fitness)fitness.clone());

                    solution = allocationPolicy;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set the best solution as the solution in the last generation
        result.setBestExpression(expression);
        result.setBestSolution(solution);
        result.setBestTrainFitness(fitness);
        result.setBestTestFitness((Fitness)fitness.clone());

        return result;
    }

    public static GPResult readCCSolutionFromFile(File file,
                                                  Problem problem,
                                                  FitnessType fitnessType) {
        CCGPHHProblem prob = (CCGPHHProblem)problem;

        GPResult result = new GPResult();

        String line;
        Fitness fitness = null;
        AllocationPolicy solution = null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (!(line = br.readLine()).equals("Best Individual of Run:")) {
                if (line.startsWith("Generation")) {
                    List<AllocationPolicy> policies = new ArrayList<>();

                    br.readLine(); // Best Individual:
                    line = br.readLine(); // Subpopulation x:
                    while (line.startsWith("Subpopulation")) {
                        br.readLine(); // Evaluated: xxx
                        line = br.readLine(); // Fitness
                        fitness = readFitnessFromLine(line, fitnessType);
                        br.readLine(); // Tree 0:

                        String expression = br.readLine();

                        expression = LispUtils.simplifyExpression(expression);
                        policies.add(new GPAllocationPolicy(prob.getPoolFilter(),
                                LispUtils.parseExpression(expression,
                                        UCARPPrimitiveSet.wholePrimitiveSet())));

                        line = br.readLine(); // Subpopulation x: or blank
                    }

                    AllocationPolicy[] policyArray = new AllocationPolicy[policies.size()];
                    policyArray = policies.toArray(policyArray);
                    AllocationPolicy ensemblePolicy = new EnsemblePolicy(prob.getPoolFilter(),
                            policyArray, prob.getCombiner());

                    result.addSolution(ensemblePolicy);
                    result.addTrainFitness(fitness);
                    result.addTestFitness((Fitness)fitness.clone());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set the best solution as the solution in the last generation
        result.setBestSolution(result.getSolutionAtGen(result.getSolutions().size()-1));
        result.setBestTrainFitness(result.getTrainFitnessAtGen(result.getTrainFitnesses().size()-1));
        result.setBestTestFitness((Fitness)fitness.clone());

        return result;
    }

    public static GPResult readRFSolutionFromFile(File file,
                                                  Problem problem,
                                                  FitnessType fitnessType) {
        CCGPHHProblem prob = (CCGPHHProblem)problem;

        GPResult result = new GPResult();

        String line;
        Fitness fitness = null;
        AllocationPolicy solution = null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (!(line = br.readLine()).equals("Best Individual of Run:")) {
                if (line.startsWith("Generation")) {
                    List<AllocationPolicy> policies = new ArrayList<>();

                    br.readLine(); // Best Individual:
                    line = br.readLine(); // Subpopulation x:
                    while (line.startsWith("Subpopulation")) {
                        br.readLine(); // Evaluated: xxx
                        line = br.readLine(); // Fitness
                        fitness = readFitnessFromLine(line, fitnessType);
                        br.readLine(); // Tree 0:

                        String expression = br.readLine();

                        expression = LispUtils.simplifyExpression(expression);
                        policies.add(new GPAllocationPolicy(prob.getPoolFilter(),
                                LispUtils.parseExpression(expression,
                                        UCARPPrimitiveSet.wholePrimitiveSet())));

                        line = br.readLine(); // Subpopulation x: or blank
                    }

                    AllocationPolicy[] policyArray = new AllocationPolicy[policies.size()];
                    policyArray = policies.toArray(policyArray);
                    AllocationPolicy ensemblePolicy = new EnsemblePolicy(prob.getPoolFilter(),
                            policyArray, prob.getCombiner());

                    result.addSolution(ensemblePolicy);
                    result.addTrainFitness(fitness);
                    result.addTestFitness((Fitness)fitness.clone());
                }
            }

            // read the final ensemble (random forest)
            List<AllocationPolicy> policies = new ArrayList<>();

            //br.readLine(); // Best Individual:
            line = br.readLine(); // Subpopulation x:
            while (line.startsWith("Subpopulation")) {
                br.readLine(); // Evaluated: xxx
                line = br.readLine(); // Fitness
                fitness = readFitnessFromLine(line, fitnessType);
                br.readLine(); // Tree 0:

                String expression = br.readLine();

                expression = LispUtils.simplifyExpression(expression);
                policies.add(new GPAllocationPolicy(prob.getPoolFilter(),
                        LispUtils.parseExpression(expression,
                                UCARPPrimitiveSet.wholePrimitiveSet())));

                line = br.readLine(); // Subpopulation x: or null

                if (line == null)
                    break;
            }

            AllocationPolicy[] policyArray = new AllocationPolicy[policies.size()];
            policyArray = policies.toArray(policyArray);
            AllocationPolicy ensemblePolicy = new EnsemblePolicy(prob.getPoolFilter(),
                    policyArray, prob.getCombiner());

            // Set the best solution as the final ensemble
            result.setBestSolution(ensemblePolicy);
            result.setBestTrainFitness(fitness);
            result.setBestTestFitness((Fitness)fitness.clone());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static Fitness readFitnessFromLine(String line,
                                               FitnessType fitnessType) {
        switch (fitnessType) {
            case SIMPLE_FITNESS:
                return readSimpleFitnessFromLine(line);
            default:
                return null;
        }
    }

    private static Fitness readSimpleFitnessFromLine(String line) {
        String[] segments = line.split("\\[|\\]");
        double fitness = Double.valueOf(segments[1]);
        MultiObjectiveFitness f = new MultiObjectiveFitness();
        f.objectives = new double[1];
        f.objectives[0] = fitness;

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
                generationalTimeStat.addValue(Double.valueOf(commaSegments[1]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return generationalTimeStat;
    }
}
