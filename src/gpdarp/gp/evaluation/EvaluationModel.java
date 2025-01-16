package gpdarp.gp.evaluation;

import ec.EvolutionState;
import ec.Fitness;
import ec.util.Parameter;
import gpdarp.core.Instance;
import gpdarp.core.Objective;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.representation.Solution;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The evaluation model for evaluating individuals in GPHH.
 *
 * @author gphhucarp, William Huang
 */
public abstract class EvaluationModel {
    public static final String P_OBJECTIVES = "objectives";
    public static final String P_INSTANCES = "instances";
    public static final String P_DATAPATH = "datapath";
    public static final String P_ROTATING = "rotating";
    public static final String P_BATCHSIZE = "batchsize";
    public static final String P_VALIDATING = "validating";
    public static final String P_VALIDATIONS = "validations";
    public static final String P_VALPATH = "valpath";
    public static final String P_NORMALISE = "normalise";

    protected List<Objective> objectives;
    protected List<Instance> instanceSamples;
    protected boolean rotating;
    protected int batchsize;
    protected int rotationIndex = 0;
    protected List<Instance> validationSamples;
    protected boolean validating;
    protected boolean normalise;
    protected Map<Pair<Integer, Objective>, Double> objRefValueMap;
    protected Map<Pair<Integer, Objective>, Double> valObjRefValueMap;

    // Getters
    public List<Objective> getObjectives() {
        return objectives;
    }
    public boolean isRotating() { return rotating; }
    public int getBatchsize() { return batchsize; }
    public int getRotationIndex() { return rotationIndex; }
    public List<Instance> getInstanceSamples() {
        return instanceSamples;
    }

    /**
     * Get the objective reference value of a particular instance and an objective.
     *
     * @param index the index of the instance.
     * @param objective the objective.
     * @return the corresponding objective reference value.
     */
    public double getObjRefValue(int index, Objective objective) {
        return objRefValueMap.get(Pair.of(index, objective));
    }

    /**
     * Get the objective reference value of a particular validation instance and an objective.
     *
     * @param index the index of the validation instance.
     * @param objective the objective.
     * @return the corresponding objective reference value.
     */
    public double getValObjRefValue(int index, Objective objective) {
        return valObjRefValueMap.get(Pair.of(index, objective));
    }

    /**
     * Set up the evaluation procedure from ECJ to execute within the problem model.
     *
     * @param state snapshot of the ECJ evolutionary process.
     * @param base utility object for extracting parameters from a custom defined ECJ database.
     */
    public void setup(final EvolutionState state, final Parameter base) {
        // get the objectives
        Parameter p = base.push(P_OBJECTIVES);
        int numObjectives = state.parameters.getIntWithDefault(p, null, 0);

        if (numObjectives == 0) {
            System.err.println("ERROR:");
            System.err.println("No objective is specified.");
            System.exit(1);
        }

        objectives = new ArrayList<>();
        for (int i = 0; i < numObjectives; i++) {
            p = base.push(P_OBJECTIVES).push("" + i);
            String objectiveName = state.parameters.getStringWithDefault(p, null, "");
            Objective objective = Objective.get(objectiveName);

            objectives.add(objective);
        }

        // set up the instances
        p = base.push(P_INSTANCES);
        int numInstances = state.parameters.getIntWithDefault(p, null, 0);

        if (numInstances == 0) {
            System.err.println("ERROR:");
            System.err.println("No instance is provided.");
            System.exit(1);
        }

        // the path containing the dataset of interest
        p = base.push(P_DATAPATH);
        String datapath = state.parameters.getStringWithDefault(p, null, "");

        instanceSamples = new ArrayList<>();
        for (int i = 0; i < numInstances; i++) {
            File file = new File(String.format("%s/%d.txt", datapath, i+1));
            Instance instance = Instance.readFromFile(file);
            Instance original = Instance.readFromFile(file);
            instance.setOriginalCopy(original);
            instanceSamples.add(instance);
        }

        // set up validation set if it is specified
        p = base.push(P_VALIDATING);
        validating = state.parameters.getBoolean(p, null, false);

        if (validating) {
            p = base.push(P_VALIDATIONS);
            int numVals = state.parameters.getIntWithDefault(p, null, 0);

            if (numVals == 0) {
                System.err.println("ERROR:");
                System.err.println("No validation instances are provided.");
                System.exit(1);
            }

            p = base.push(P_VALPATH);
            String valpath = state.parameters.getStringWithDefault(p, null, "");

            validationSamples = new ArrayList<>();
            for (int i = 0; i < numVals; i++) {
                File file = new File(String.format("%s/%d.txt", valpath, i+1));
                Instance instance = Instance.readFromFile(file);
                Instance original = Instance.readFromFile(file);
                instance.setOriginalCopy(original);
                validationSamples.add(instance);
            }
        }

        // determine whether instance sample rotation should occur, and by how much
        p = base.push(P_ROTATING);
        this.rotating = state.parameters.getBoolean(p, null, false);
        p = base.push(P_BATCHSIZE);
        this.batchsize = state.parameters.getIntWithDefault(p, null, 5);

        // determine whether normalisation of the test set should occur
        p = base.push(P_NORMALISE);
        this.normalise = state.parameters.getBoolean(p, null, true);

        // calculate the initial objective reference values
        objRefValueMap = new HashMap<>();
        valObjRefValueMap = new HashMap<>();
        calcObjRefValueMap();
    }

    /**
     * Calculate the objective reference values from a baseline policy defined in the Objective class.
     * Do it for the validation set as well if the validation set is specified.
     */
    public void calcObjRefValueMap() {
        int index = 0;
        for (Instance sample : instanceSamples) {
            ReactiveDecisionProcess dp = DecisionProcess.initReactive(sample, 
                    Objective.refVehiclePolicy(), Objective.refRequestPolicy());

            // get the objective reference values by applying the reference routing policy
            dp.run();
            Solution solution = dp.getState().getSolution();
            for (Objective objective : objectives) {
                double objValue = solution.objValue(objective);
                objRefValueMap.put(Pair.of(index, objective), objValue);
                index++;
            }
            dp.reset();
        }

        if (validating) {
            index = 0;
            for (Instance sample : validationSamples) {
                ReactiveDecisionProcess dp = DecisionProcess.initReactive(sample,
                        Objective.refVehiclePolicy(), Objective.refRequestPolicy());

                // get the objective reference values by applying the reference routing policy
                dp.run();
                Solution solution = dp.getState().getSolution();
                for (Objective objective : objectives) {
                    double objValue = solution.objValue(objective);
                    valObjRefValueMap.put(Pair.of(index, objective), objValue);
                    index++;
                }
                dp.reset();
            }
        }
    }

    public void rotate() {
        rotationIndex += batchsize;
        if (rotationIndex + batchsize > instanceSamples.size()) {
            rotationIndex = 0;
        }
    }

    /**
     * Evaluate an individual (a combination of policies) using this evaluation model.
     *
     * @param vehiclePolicy the vehicle allocation policy to be evaluated.
     * @param requestPolicy the request allocation policy to be evaluated.
     * @param fitness the fitness of the individual.
     * @param state the evolution state.
     */
    public abstract void evaluate(VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy,
                                  Fitness fitness, EvolutionState state);

    /**
     * Evaluate an individual (a combination of policies) using this evaluation model.
     * The fitness is original --- without normalisation.
     *
     * @param vehiclePolicy the vehicle allocation policy to be evaluated.
     * @param requestPolicy the request allocation policy to be evaluated.
     * @param fitness the fitness of the individual.
     * @param state the evolution state.
     * @return the solutions for the individual against all instances.
     */
    public abstract List<Solution> evaluateOriginal(VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy,
                                          Fitness fitness, EvolutionState state);
}
