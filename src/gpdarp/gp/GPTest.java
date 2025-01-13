package gpdarp.gp;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Evolve;
import ec.Fitness;
import ec.gp.GPNode;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;
import ec.util.ParameterDatabase;
import gpdarp.core.*;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.allocationpolicy.requestpolicy.GPRequestPolicy;
import gpdarp.decisionprocess.allocationpolicy.vehiclepolicy.GPVehiclePolicy;
import gpdarp.gp.evaluation.EvaluationModel;
import gpdarp.gp.io.FitnessType;
import gpdarp.gp.io.GPResult;
import gpdarp.gp.io.SolutionType;
import gpdarp.representation.Solution;
import gpdarp.representation.route.Route;
import gputils.UniqueTerminalsGatherer;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The main program of the GP test process.
 * It reads the out.stat files from the training path subject to the solution and fitness types.
 * Then it tests all the solutions read from the training files on the test set.
 * Finally, it writes all the related information to a csv file.
 *
 * @author gphhucarp
 */
public class GPTest {
    public static final String P_POLICY_TYPE = "policy-type"; // manual or gp-evolved
    public static final String P_MANUAL_VEHICLE_POLICIES = "manual-vehicle-policies";
    public static final String P_MANUAL_REQUEST_POLICIES = "manual-request-policies";
    public static final String P_TRAIN_PATH = "train-path"; // path of the out.stat files of the training
    public static final String P_SOLUTION_TYPE = "solution-type"; // solution type, e.g. a single routing policy
    public static final String P_FITNESS_TYPE = "fitness-type"; // fitness type, e.g. multiobjective fitness
    public static final String P_NUM_TRAINS = "num-trains"; // number of trains (out.stat files)

    public static void main(String[] args) {
        ParameterDatabase parameters = Evolve.loadParameterDatabase(args);
        EvolutionState state = Evolve.initialize(parameters, 0);
        Parameter p;

        // set up the evaluator, essentially the test evaluation model
        p = new Parameter(EvolutionState.P_EVALUATOR);
        state.evaluator = (Evaluator)
                (parameters.getInstanceForParameter(p, null, Evaluator.class));
        state.evaluator.setup(state, p);

        // read the path of the training out.stat files.
        p = new Parameter(P_TRAIN_PATH);
        String trainPath = parameters.getStringWithDefault(p, null, "");

        // read the solution type, e.g. a single routing policy or ensemble
        p = new Parameter(P_SOLUTION_TYPE);
        String stString = parameters.getStringWithDefault(p, null, "");
        SolutionType solutionType = SolutionType.get(stString);

        // read the fitness type, e.g. a multiobjective fitness
        p = new Parameter(P_FITNESS_TYPE);
        String ftString = parameters.getStringWithDefault(p, null, "");
        FitnessType fitnessType = FitnessType.get(ftString);

        // read the number of trains, i.e. the number of out.stat files
        p = new Parameter(P_NUM_TRAINS);
        int numTrains = parameters.getIntWithDefault(p, null, 1);

        // the fields for testing
        ReactiveGPHHProblem testProblem = (ReactiveGPHHProblem)state.evaluator.p_problem;
        EvaluationModel testEvaluationModel = testProblem.evaluationModel;

        // read the tested policy(ies)
        p = new Parameter(P_POLICY_TYPE);
        String policyType = parameters.getStringWithDefault(p, null, "");

        if (policyType.equals("gp-evolved")) {
            // create new subdirectory
            File writtenPath = new File(trainPath + "test");
            if (!writtenPath.exists()) {
                writtenPath.mkdirs();
            }
            String filePath = writtenPath + "/gp";

            // read the results from the training files
            List<GPResult> results = new ArrayList<>();

            // start testing the rules
            System.out.println("Test rules from path " + trainPath);

            List<Solution> solutions;

            for (int i = 0; i < numTrains; i++) {
                System.out.println("Testing run " + i);

                File sourceFile = new File(trainPath + "job." + i + ".out.stat");

                // read the rules to a result class
                GPResult result = GPResult.readFromFile(sourceFile, state.evaluator.p_problem, solutionType, fitnessType);

                // read the time from the .stat.csv file
                File timeFile = new File(trainPath + "job." + i + ".stat.csv");
                result.setTimeStat(GPResult.readTimeFromFile(timeFile));

                // test the rules for each generation
                long start = System.currentTimeMillis();

                for (int j = 0; j < result.getSolutions().size(); j++) {
                    Pair<VehiclePolicy, RequestPolicy> solution = result.getSolutionAtGen(j);
                    solutions = testEvaluationModel.evaluateOriginal(solution.getLeft(), solution.getRight(),
                            result.getTestFitnessAtGen(j), state);

                    double avgDecisionTime;
                    if (solutions.isEmpty()) {
                        avgDecisionTime = 0;
                    }
                    else {
                        avgDecisionTime = solutions.stream()
                                .map(Solution::getAvgDecisionTime)
                                .mapToDouble(Double::doubleValue)
                                .sum() / result.getSolutions().size();
                    }

                    result.addAvgDecisionTime(avgDecisionTime);

                    System.out.println("Generation " + j + ": test fitness = " +
                            ((KozaFitness)result.getTestFitnessAtGen(j)).standardizedFitness());
                }

                // test the best rule
                Pair<VehiclePolicy, RequestPolicy> bestSolution = result.getBestSolution();
                solutions = testEvaluationModel.evaluateOriginal(bestSolution.getLeft(), bestSolution.getRight(),
                        result.getBestTestFitness(), state);

                System.out.println("Best individual: test fitness = " +
                        ((KozaFitness)result.getBestTestFitness()).standardizedFitness());

                long finish = System.currentTimeMillis();
                long duration = finish - start;
                System.out.println("Duration = " + duration + " ms.");

                results.add(result);

                // write to files
                writeSolution(solutions, filePath + "-" + i);
            }

            File csvFile = new File(filePath + ".csv");

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getAbsoluteFile()));
                // write the title
                writer.write(csvTitle());
                writer.newLine();

                for (int i = 0; i < numTrains; i++) {
                    GPResult result = results.get(i);

                    // used to calculate the number of unique terminals
                    UniqueTerminalsGatherer gatherer = new UniqueTerminalsGatherer();

                    // write the test results for each generation
                    int numTerminals = 0;
                    int numUniqueTerminals = 0;

                    for (int j = 0; j < result.getSolutions().size(); j++) {
                        gatherer = new UniqueTerminalsGatherer();
                        numTerminals = 0;
                        numUniqueTerminals = 0;

                        Pair<VehiclePolicy, RequestPolicy> solution = result.getSolutionAtGen(j);
                        GPVehiclePolicy tree1 = (GPVehiclePolicy) solution.getLeft();
                        GPRequestPolicy tree2 = (GPRequestPolicy) solution.getRight();

                        numTerminals += tree1.getGPTree().child.numNodes(GPNode.NODESEARCH_ALL);
                        numTerminals += tree2.getGPTree().child.numNodes(GPNode.NODESEARCH_ALL);

                        numUniqueTerminals += tree1.getGPTree().child.numNodes(gatherer);
                        numUniqueTerminals += tree2.getGPTree().child.numNodes(gatherer);

                        writer.write(i + "," + j + ",0," + numTerminals + "," + numUniqueTerminals + "," +
                                fitnessString(result, j) + result.getTimeAtGen(j) +
                                "," + result.getAvgDecisionTimeAtGen(j));
                        writer.newLine();
                    }

                    writer.write(i + "," + "-1" + ",0," + numTerminals + "," + numUniqueTerminals + "," +
                            fitnessString(result, -1) + result.getTimeAtGen(result.getBestIndex()) +
                            "," + result.getAvgDecisionTimeAtGen(result.getBestIndex()));
                    writer.newLine();
                }
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            // create new subdirectory
            File writtenPath = new File(trainPath + "test");
            if (!writtenPath.exists()) {
                writtenPath.mkdirs();
            }
            String filePath = writtenPath + "/manual";
            File csvFile = new File(filePath + ".csv");

            Parameter vb = new Parameter(P_MANUAL_VEHICLE_POLICIES);
            Parameter rb = new Parameter(P_MANUAL_REQUEST_POLICIES);
            int manualPolicies = parameters.getIntWithDefault(vb, null, 0);

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getAbsoluteFile()));
                writer.write("VehiclePolicy,RequestPolicy,Fitness");
                writer.newLine();

                for (int i = 0; i < manualPolicies; i++) {
                    p = vb.push("" + i);
                    VehiclePolicy vehiclePolicy = (VehiclePolicy) parameters.getInstanceForParameter(
                            p, null, VehiclePolicy.class);
                    p = rb.push("" + i);
                    RequestPolicy requestPolicy = (RequestPolicy) parameters.getInstanceForParameter(
                            p, null, RequestPolicy.class);

                    KozaFitness fit = new KozaFitness();
                    List<Solution> solutions = testEvaluationModel.evaluateOriginal(vehiclePolicy, requestPolicy, fit, state);
                    writeSolution(solutions, filePath + "-" + i);

                    writer.write(String.format("%s,%s,%f", vehiclePolicy.getName(), requestPolicy.getName(),
                            fit.standardizedFitness()));
                    writer.newLine();
                }
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String csvTitle() {
        return "Run,Generation,Subpop,Size,UniqueTerminals,TrainFitness,TestFitness,TrainTime,AvgDecisionTime";
    }

    private static String fitnessString(GPResult result, int gen) {
        String s = "";

        KozaFitness trainFit = (KozaFitness) result.getBestTrainFitness();
        KozaFitness testFit = (KozaFitness) result.getBestTestFitness();

        if (gen != -1) {
            trainFit = (KozaFitness) result.getTrainFitnessAtGen(gen);
            testFit = (KozaFitness) result.getTestFitnessAtGen(gen);
        }

        KozaFitness simpleTrainFit = trainFit;
        KozaFitness simpleTestFit = testFit;
        s += simpleTrainFit.standardizedFitness();
        s += ",";
        s += simpleTestFit.standardizedFitness();
        s += ",";

        return s;
    }

    private static void writeSolution(List<Solution> solutions, String filePath) {
        Random rand = new Random();
        int sample = rand.nextInt(solutions.size());
        Solution solution = solutions.get(sample);

        int numVehicles = solution.getRoutes().size();
        int numRequests = solution.getNumRequests();
        String name = solution.getName().replace(".txt", "");

        File solFile = new File(filePath + ".sol");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(solFile.getAbsoluteFile()));
            writer.write(String.format("%d %d %s", numVehicles, numRequests, name));
            writer.newLine();

            for (int i = 0; i < numVehicles; i++) {
                Route route = solution.getRoutes().get(i);
                String visits = String.valueOf(i+1);
                String times = String.valueOf(i+1);

                for (Arc arc : route.getArcs()) {
                    Node node = arc.to();
                    switch (node.getType()) {
                        case PICKUP -> visits += " " + node.getRequest().getId();
                        case DROPOFF -> visits += " -" + node.getRequest().getId();
                        case STATION -> visits += " " + ((Station)node).getId();
                    }
                    times += " " + node.getArrivalTime();
                }

                writer.write(visits);
                writer.newLine();
                writer.write(times);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
