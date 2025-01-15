package gpdarp.gp;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Evolve;
import ec.gp.GPNode;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;
import ec.util.ParameterDatabase;
import gpdarp.core.*;
import gpdarp.decisionprocess.DecisionProcess;
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
 * It reads the out.stat files from the training path.
 * For each generation in a run, it does the following:
 * - read the solution,
 * - determine whether the best individual in the generation generalised well to the validation set,
 * - if so, evaluate that individual against the test set, and,
 * - if it doesn't generalise well to the test set, identify the infeasible instances and write their states to file.
 * Finally, it writes all the related information to a csv file.
 *
 * @author gphhucarp, William Huang
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
            System.out.println("Test rules from path " + trainPath);
            List<GPResult> results = new ArrayList<>();
            List<Solution> solutions;

            // create subdirectory for test output
            File outputPath = new File(trainPath + "test");
            if (!outputPath.exists()) {
                outputPath.mkdirs();
            }
            String filePath = outputPath + "/gp";

            // create subdirectory for debug output
            File debugPath = new File(trainPath + "debug");
            if (!debugPath.exists()) {
                debugPath.mkdirs();
            }

            for (int i = 0; i < numTrains; i++) {
                System.out.println("Testing run " + i);

                File sourceFile = new File(trainPath + "job." + i + ".out.stat");
                GPResult result = GPResult.readFromFile(sourceFile, state.evaluator.p_problem, solutionType, fitnessType);

                File timeFile = new File(trainPath + "job." + i + ".stat.csv");
                result.setTimeStat(GPResult.readTimeFromFile(timeFile));

                long start = System.currentTimeMillis();

                // test the rules for each generation
                for (int j = 0; j < result.getSolutions().size(); j++) {
                    double val = result.getValidationAtGen(j);

                    if (val >= 0 && val < Double.MAX_VALUE) {
                        Pair<VehiclePolicy, RequestPolicy> solution = result.getSolutionAtGen(j);
                        solutions = testEvaluationModel.evaluateOriginal(solution.getLeft(), solution.getRight(),
                                result.getTestFitnessAtGen(j), state);
                        solutions.removeIf(s -> s.isFeasible());

                        if (((KozaFitness)result.getTestFitnessAtGen(j)).standardizedFitness() > 1000000) {
                            System.out.printf("Generation %d: num failed tests = %d\n", j, solutions.size());

                            for (int k = 0; k < solutions.size(); k++) {
                                Solution sol = solutions.get(k);
                                DecisionProcess dp = sol.getDp();
                                List<Request> waitingRequests = dp.getWaitingList();
                                List<Vehicle> vehicles = dp.getState().getInstance().getVehicles();

                                File txtFile = new File(debugPath + String.format("/%d-%d-%d.txt", i, j, k));

                                try {
                                    BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile.getAbsoluteFile()));

                                    writer.write("REQUESTS");
                                    writer.newLine();
                                    for (Request request : waitingRequests) {
                                        writer.write(request.toString());
                                        writer.newLine();
                                    }

                                    writer.newLine();

                                    writer.write("VEHICLES");
                                    writer.newLine();
                                    for (Vehicle vehicle : vehicles) {
                                        writer.write(vehicle.toString());
                                        writer.newLine();
                                    }

                                    writer.newLine();
                                    writer.newLine();

                                    writer.write("ROUTES");
                                    writer.newLine();
                                    for (Route route : sol.getRoutes()) {
                                        writer.write(route.toString());
                                        writer.newLine();
                                    }
                                    writer.newLine();
                                    writer.newLine();


                                    writer.write("VP PRIORITIES");
                                    writer.newLine();
                                    VehiclePolicy vp = dp.getVehiclePolicy();
                                    for (Request request : waitingRequests) {
                                        List<Pair<Vehicle, Route>> pool = vp.debugNext(dp.getState(), request);
                                        for (Pair<Vehicle, Route> pair : pool) {
                                            Vehicle vehicle = pair.getLeft();
                                            Route route = pair.getRight();
                                            String res = String.format("vehicle %d's priority for request %d: %f",
                                                    vehicle.getId(), request.getId(), route.getPriority());
                                            writer.write(res);
                                            writer.newLine();
                                            writer.write(route.getViolationLog());
                                            writer.newLine();
                                        }
                                    }

                                    writer.newLine();

                                    writer.write("RP PRIORITIES");
                                    writer.newLine();
                                    RequestPolicy rp = dp.getRequestPolicy();
                                    for (Vehicle vehicle : vehicles) {
                                        List<Pair<Request, Route>> pool = rp.debugNext(vehicle, dp.getState(), new ArrayList<>(waitingRequests));
                                        for (Pair<Request, Route> pair : pool) {
                                            Request request = pair.getLeft();
                                            Route route = pair.getRight();
                                            String res = String.format("request %d's priority for vehicle %d: %f",
                                                    request.getId(), vehicle.getId(), route.getPriority());
                                            writer.write(res);
                                            writer.newLine();
                                            writer.write(route.getViolationLog());
                                            writer.newLine();
                                        }
                                    }
                                    writer.newLine();

                                    writer.write("TREES");
                                    writer.newLine();
                                    Pair<String, String> expr = result.getExpressionAtGen(j);
                                    writer.write(expr.getLeft());
                                    writer.newLine();
                                    writer.write(expr.getRight());
                                    writer.newLine();

                                    writer.close();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                // test the best rule
                Pair<VehiclePolicy, RequestPolicy> bestSolution = result.getBestSolution();
                solutions = testEvaluationModel.evaluateOriginal(bestSolution.getLeft(), bestSolution.getRight(),
                        result.getBestTestFitness(), state);

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

                result.setAvgDecisionTime(avgDecisionTime);

                System.out.println("Best individual: test fitness = " +
                        ((KozaFitness)result.getBestTestFitness()).standardizedFitness());

                long finish = System.currentTimeMillis();
                long duration = finish - start;
                System.out.println("Duration = " + duration + " ms.");

                results.add(result);

                // write one of the solutions to output for correctness checking
                writeSolution(solutions, filePath + "-" + i);
            }

            File csvFile = new File(filePath + ".csv");

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getAbsoluteFile()));
                writer.write(csvTitle());
                writer.newLine();

                List<Integer> size1 = new ArrayList<>();
                List<Integer> size2 = new ArrayList<>();
                List<Integer> unique1 = new ArrayList<>();
                List<Integer> unique2 = new ArrayList<>();

                for (int i = 0; i < numTrains; i++) {
                    GPResult result = results.get(i);

                    UniqueTerminalsGatherer gatherer1;
                    UniqueTerminalsGatherer gatherer2;

                    int numTerminals1;
                    int numTerminals2;
                    int numUnique1;
                    int numUnique2;

                    for (int j = 0; j < result.getSolutions().size(); j++) {
                        gatherer1 = new UniqueTerminalsGatherer();
                        gatherer2 = new UniqueTerminalsGatherer();

                        Pair<VehiclePolicy, RequestPolicy> solution = result.getSolutionAtGen(j);
                        GPVehiclePolicy tree1 = (GPVehiclePolicy) solution.getLeft();
                        GPRequestPolicy tree2 = (GPRequestPolicy) solution.getRight();

                        numTerminals1 = tree1.getGPTree().child.numNodes(GPNode.NODESEARCH_ALL);
                        numTerminals2 = tree2.getGPTree().child.numNodes(GPNode.NODESEARCH_ALL);

                        numUnique1 = tree1.getGPTree().child.numNodes(gatherer1);
                        numUnique2 = tree2.getGPTree().child.numNodes(gatherer2);

                        writer.write(i + "," + j + ",0," +
                                numTerminals1 + "," + numUnique1 + "," +
                                numTerminals2 + "," + numUnique2 + "," +
                                fitnessString(result, j) + result.getTimeAtGen(j) +
                                "," + result.getAvgDecisionTime());
                        writer.newLine();

                        size1.add(numTerminals1);
                        size2.add(numTerminals2);
                        unique1.add(numUnique1);
                        unique2.add(numUnique2);
                    }

                    int bestIndex = result.getBestIndex();

                    writer.write(i + "," + "-1" + ",0," +
                            size1.get(bestIndex) + "," + unique1.get(bestIndex) + "," +
                            size2.get(bestIndex) + "," + unique2.get(bestIndex) + "," +
                            fitnessString(result, -1) + result.getTimeAtGen(bestIndex) +
                            "," + result.getAvgDecisionTime());
                    writer.newLine();
                }
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            // create subdirectory for manual test output
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
        return "Run,Generation,Subpop,VPSize,VPUniqueTerminals,RPSize,RPUniqueTerminals," +
                "TrainFitness,TestFitness,TrainTime,AvgDecisionTime";
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
