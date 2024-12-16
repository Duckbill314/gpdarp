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
    public static final String P_DATASET = "dataset";

    protected List<Objective> objectives;
    protected List<Instance> instanceSamples;
    protected Map<Pair<Integer, Objective>, Double> objRefValueMap;

    // Getters
    public List<Objective> getObjectives() {
        return objectives;
    }
    public List<Instance> getInstanceSamples() {
        return instanceSamples;
    }

    /**
     * Get the objective reference value of a particular decision process and an objective.
     *
     * @param index the index of the decision process.
     * @param objective the objective.
     *
     * @return the corresponding objective reference value.
     */
    public double getObjRefValue(int index, Objective objective) {
        return objRefValueMap.get(Pair.of(index, objective));
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

        // which dataset to use (train or test)
        p = base.push(P_DATASET);
        String dataset = state.parameters.getStringWithDefault(p, null, "train");

        instanceSamples = new ArrayList<>();
        for (int i = 0; i < numInstances; i++) {
            File file = new File(String.format("data/darp/%s/%d.txt", dataset, i+1));
            Instance instance = Instance.readFromFile(file);
            instanceSamples.add(instance);
        }

        // calculate the initial objective reference values
        objRefValueMap = new HashMap<>();
        calcObjRefValueMap();
    }

    /**
     * Calculate the objective reference values from a baseline policy defined in the Objective class.
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
     */
    public abstract void evaluateOriginal(VehiclePolicy vehiclePolicy, RequestPolicy requestPolicy,
                                          Fitness fitness, EvolutionState state);
}
