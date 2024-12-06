package gpdarp.gp.evaluation;

import ec.EvolutionState;
import ec.Fitness;
import ec.util.Parameter;
import gpdarp.core.Instance;
import gpdarp.core.Objective;
import gpdarp.representation.Solution;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.RoutingPolicy;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The evaluation model for evaluating individuals in GPHH.
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
     * @param index the index of the decision process.
     * @param objective the objective.
     * @return the corresponding objective reference value.
     */
    public double getObjRefValue(int index, Objective objective) {
        return objRefValueMap.get(Pair.of(index, objective));
    }

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
     * Calculate the objective reference values.
     */
    public void calcObjRefValueMap() {
        int index = 0;
        for (InstanceSamples iSamples : instanceSamples) {
            for (long seed : iSamples.getSeeds()) {
                // create a new reactive decision process from the based intance and the seed.
                ReactiveDecisionProcess dp =
                        DecisionProcess.initReactive(iSamples.getBaseInstance(),
                                seed, Objective.refReactiveRoutingPolicy());

                // get the objective reference values by applying the reference routing policy.
                dp.run();
                Solution<NodeSeqRoute> solution = dp.getState().getSolution();
                for (Objective objective : objectives) {
                    double objValue = solution.objValue(objective);
                    objRefValueMap.put(Pair.of(index, objective), objValue);
                    index ++;
                }
                dp.reset();
            }
        }
    }

    /**
     * Evaluate an individual (a policy plus a plan) using this evaluation model.
     * @param policy the policy to be evaluated.
     * @param fitness the fitness of the individual.
     * @param state the evolution state.
     */
    public abstract void evaluate(RoutingPolicy policy, Fitness fitness, EvolutionState state);

    /**
     * Evaluate an individual (a policy plus a plan) using this evaluation model.
     * The fitness is original --- without normalisation.
     * @param policy the policy to be evaluated.
     * @param fitness the fitness of the individual.
     * @param state the evolution state.
     */
    public abstract void evaluateOriginal(RoutingPolicy policy, Fitness fitness, EvolutionState state);
}
