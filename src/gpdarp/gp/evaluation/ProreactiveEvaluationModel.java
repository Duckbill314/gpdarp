package gpdarp.gp.evaluation;

import ec.EvolutionState;
import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import gpdarp.core.InstanceSamples;
import gpdarp.core.Objective;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.RoutingPolicy;
import gpdarp.decisionprocess.proreactive.ProreativeDecisionProcess;
import gpdarp.decisionprocess.routingpolicy.FeasibilityPolicy;
import gpdarp.representation.Solution;

/**
 * A proactive-reactive evaluation model is a set of proactive-reactive decision process.
 * It evaluates a proactive-reactive routing policy and a task sequence plan,
 * by applying them on each decision process,
 * and returning the average normalised objective values across the processes.
 *
 * It includes
 *  - A list of proactive-reactive decision processes.
 *  - The reference objective value map, indicating the reference value
 *    of a given decision process and a given objective.
 *
 * Created by gphhucarp on 31/08/17.
 */

public class ProreactiveEvaluationModel extends EvaluationModel {

    @Override
    public void evaluate(RoutingPolicy policy, Solution<TaskSeqRoute> plan,
                         Fitness fitness, EvolutionState state) {
        double[] fitnesses = new double[objectives.size()];

        int numdps = 0;
        for (InstanceSamples iSamples : instanceSamples) {
            for (long seed : iSamples.getSeeds()) {
                ProreativeDecisionProcess dp = DecisionProcess.initProreactive(
                        iSamples.getBaseInstance(), seed, policy, plan);

                dp.run();
                Solution<NodeSeqRoute> solution = dp.getState().getSolution();
                for (int j = 0; j < fitnesses.length; j++) {
                    Objective objective = objectives.get(j);
                    double normObjValue =
                            solution.objValue(objective); // / getObjRefValue(i, objective);
                    fitnesses[j] += normObjValue;
                }
                dp.reset();

                numdps ++;
            }
        }

        for (int j = 0; j < fitnesses.length; j++) {
            fitnesses[j] /= numdps;
        }

        MultiObjectiveFitness f = (MultiObjectiveFitness)fitness;
        f.setObjectives(state, fitnesses);
    }

    @Override
    public void evaluateOriginal(RoutingPolicy policy,
                                 Solution<TaskSeqRoute> plan,
                                 Fitness fitness, EvolutionState state) {
        double[] fitnesses = new double[objectives.size()];

        int numdps = 0;
        for (InstanceSamples iSamples : instanceSamples) {
            for (long seed : iSamples.getSeeds()) {
                ProreativeDecisionProcess dp = DecisionProcess.initProreactive(
                        iSamples.getBaseInstance(), seed,
                        new FeasibilityPolicy(), null);

                dp.run();
                Solution<NodeSeqRoute> solution = dp.getState().getSolution();
                for (int j = 0; j < fitnesses.length; j++) {
                    Objective objective = objectives.get(j);
                    double normObjValue =
                            solution.objValue(objective);
                    fitnesses[j] += normObjValue;
                }
                dp.reset();

                numdps ++;
            }
        }

        for (int j = 0; j < fitnesses.length; j++) {
            fitnesses[j] /= numdps;
        }

        MultiObjectiveFitness f = (MultiObjectiveFitness)fitness;
        f.setObjectives(state, fitnesses);
    }
}
