package gpdarp.gp;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Evolve;
import ec.Fitness;
import ec.gp.GPNode;
import ec.gp.koza.KozaFitness;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;
import ec.util.ParameterDatabase;
import gpdarp.core.Instance;
import gpdarp.core.Objective;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.allocationpolicy.requestpolicy.GPRequestPolicy;
import gpdarp.decisionprocess.allocationpolicy.vehiclepolicy.GPVehiclePolicy;
import gpdarp.gp.evaluation.EvaluationModel;
import gpdarp.gp.io.FitnessType;
import gpdarp.gp.io.GPResult;
import gpdarp.gp.io.SolutionType;
import gputils.UniqueTerminalsGatherer;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            // read the results from the training files
            List<GPResult> results = new ArrayList<>();

            // start testing the rules
            System.out.println("Test rules from path " + trainPath);

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
                    testEvaluationModel.evaluateOriginal(solution.getLeft(), solution.getRight(),
                            result.getTestFitnessAtGen(j), state);

                    System.out.println("Generation " + j + ": test fitness = " +
                            result.getTestFitnessAtGen(j).fitness());
                }

                // test the best rule
                Pair<VehiclePolicy, RequestPolicy> bestSolution = result.getBestSolution();
                testEvaluationModel.evaluateOriginal(bestSolution.getLeft(), bestSolution.getRight(),
                        result.getBestTestFitness(), state);

                System.out.println("Best individual: test fitness = " +
                        result.getBestTestFitness().fitness());

                long finish = System.currentTimeMillis();
                long duration = finish - start;
                System.out.println("Duration = " + duration + " ms.");

                results.add(result);
            }

            // write to csv file
            File writtenPath = new File(trainPath + "test");
            if (!writtenPath.exists()) {
                writtenPath.mkdirs();
            }

            String writtenFileName = testFileName(testEvaluationModel);
            File csvFile = new File(writtenPath + "/" + writtenFileName + ".csv");

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getAbsoluteFile()));
                // write the title
                writer.write(csvTitle(fitnessType));
                writer.newLine();
                for (int i = 0; i < numTrains; i++) {
                    GPResult result = results.get(i);

                    // used to calculate the number of unique terminals
                    UniqueTerminalsGatherer gatherer = new UniqueTerminalsGatherer();

                    switch (solutionType) {
                        case SIMPLE_SOLUTION:
                            // write the test results for each generation
                            int numTerminals = 0;
                            int numUniqueTerminals = 0;

                            for (int j = 0; j < result.getSolutions().size(); j++) {
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
                                        fitnessString(result, j, fitnessType) +
                                        result.getTimeAtGen(j));
                                writer.newLine();
                            }
                            writer.write(i + "," + "-1" + ",0," + numTerminals + "," + numUniqueTerminals + "," +
                                    fitnessString(result, result.getSolutions().size()-1, fitnessType) +
                                    result.getTimeAtGen(result.getSolutions().size()-1));
                            writer.newLine();
                            break;
                        default:
                            System.err.println("Unknown solution type: " + solutionType.toString());
                            System.exit(1);
                    }
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            String writtenFileName = testFileName(testEvaluationModel);
            File csvFile = new File("manual-" + writtenFileName + ".csv");

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getAbsoluteFile()));
                writer.write("VehiclePolicy,RequestPolicy,Fitness");
                writer.newLine();

                int maxIndex = testEvaluationModel.getInstanceSamples().size()-1;
                Objective objective = testEvaluationModel.getObjectives().getFirst();
                double objValue = 0;

                for (int i = 0; i < maxIndex; i++) {
                    objValue += testEvaluationModel.getObjRefValue(i, objective);
                }

                VehiclePolicy vehiclePolicy = Objective.refVehiclePolicy();
                RequestPolicy requestPolicy = Objective.refRequestPolicy();

                writer.write(String.format("%s,%s,%f", vehiclePolicy.getName(), requestPolicy.getName(), objValue));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static String testFileName(EvaluationModel testEvaluationModel) {
        String str = "";
        for (Objective objective : testEvaluationModel.getObjectives())
            str += objective.getName() + "-";

        Instance instance = testEvaluationModel.getInstanceSamples().get(0);
        str += instance.getName() + "-" + instance.getNumVehicles() + "-";

        return str;
    }

    private static String csvTitle(FitnessType fitnessType) {
        String s = "Run,Generation,Subpop,Size,UniqueTerminals,";

        if (fitnessType == FitnessType.DIMENSION_AWARE_FITNESS)
            s += "DimensionGap,";

        s += "TrainFitness,TestFitness,Time";

        return s;
    }

    private static String fitnessString(GPResult result, int gen, FitnessType fitnessType) {
        String s = "";

        Fitness trainFit = result.getBestTrainFitness();
        Fitness testFit = result.getBestTestFitness();

        if (gen != -1) {
            trainFit = result.getTrainFitnessAtGen(gen);
            testFit = result.getTestFitnessAtGen(gen);
        }

        switch (fitnessType) {
            case SIMPLE_FITNESS:
                KozaFitness simpleTrainFit = (KozaFitness) trainFit;
                KozaFitness simpleTestFit = (KozaFitness) testFit;
                s += simpleTrainFit.standardizedFitness();
                s += ",";
                s += simpleTestFit.standardizedFitness();
                s += ",";
                break;
        }

        return s;
    }
}
