package gpdarp.gp;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Evolve;
import ec.util.Parameter;
import ec.util.ParameterDatabase;
import gpdarp.gp.io.FitnessType;
import gpdarp.gp.io.GPResult;
import gpdarp.gp.io.SolutionType;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight version of GPTest that is used to extract validation performance data.
 *
 * @author gphhucarp, William Huang
 */
public class GPValidate {
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

        // read the tested policy(ies)
        p = new Parameter(P_POLICY_TYPE);
        String policyType = parameters.getStringWithDefault(p, null, "");

        if (policyType.equals("gp-evolved")) {
            System.out.println("Validate rules from path " + trainPath);
            List<GPResult> results = new ArrayList<>();

            // create subdirectory for test output
            File outputPath = new File(trainPath + "test");
            if (!outputPath.exists()) {
                outputPath.mkdirs();
            }
            String filePath = outputPath + "/valid";

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

                int j = result.getBestIndex();

                File txtFile = new File(debugPath + String.format("/tree-%d-%d.txt", i, j));

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile.getAbsoluteFile()));

                    Pair<String, String> expr = result.getExpressionAtGen(j);
                    writer.write(expr.getLeft());
                    writer.newLine();
                    writer.write(expr.getRight());
                    writer.newLine();

                    writer.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                results.add(result);
            }

            File csvFile = new File(filePath + ".csv");

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getAbsoluteFile()));
                writer.write(csvTitle());
                writer.newLine();

                for (int i = 0; i < numTrains; i++) {
                    GPResult result = results.get(i);
                    int j = result.getBestIndex();

                    writer.write(i + "," + j + "," + result.getBestValidation());
                    writer.newLine();
                }

                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String csvTitle() {
        return "Run,Generation,ValidationFitness";
    }
}
