package gpdarp.algorithm.sopoc;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Problem;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPIndividual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;
import gpdarp.algorithm.edasls.GiantTaskSequenceIndividual;
import gpdarp.core.InstanceSamples;
import gpdarp.core.Objective;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.RoutingPolicy;
import gpdarp.decisionprocess.proreactive.ProreativeDecisionProcess;
import gpdarp.decisionprocess.routingpolicy.GPRoutingPolicy;
import gpdarp.gp.evaluation.EvaluationModel;

import java.util.List;

/**
 * The SoPoC problem.
 *
 * The problem evaluates a baseline solution together with a policy.
 * The vehicles follow the baseline solution, and at each step the policy decides
 * whether to go to the depot to refill, or continue.
 */
public class SoPoCProblem extends Problem implements GroupedProblemForm {
    public static final String P_EVAL_MODEL = "eval-model";

    protected EvaluationModel evaluationModel;

    public List<Objective> getObjectives() {
        return evaluationModel.getObjectives();
    }

    public EvaluationModel getEvaluationModel() {
        return evaluationModel;
    }

    public void rotateEvaluationModel() {
        evaluationModel.rotateSeeds();
    }

    @Override
    public void setup(final EvolutionState state, final Parameter base) {

        Parameter p = base.push(P_EVAL_MODEL);
        evaluationModel = (EvaluationModel)(
                state.parameters.getInstanceForParameter(
                        p, null, EvaluationModel.class));
        evaluationModel.setup(state, p);
    }

    @Override
    public void preprocessPopulation(EvolutionState state, Population pop, boolean[] prepareForFitnessAssessment, boolean countVictoriesOnly) {

    }

    @Override
    public void postprocessPopulation(EvolutionState state, Population pop, boolean[] assessFitness, boolean countVictoriesOnly) {

    }

    @Override
    public void evaluate(EvolutionState state,
                         Individual[] ind,
                         boolean[] updateFitness,
                         boolean countVictoriesOnly,
                         int[] subpops, int threadnum) {
        GiantTaskSequenceIndividual chromosome = (GiantTaskSequenceIndividual)ind[0];
        RoutingPolicy policy = new GPRoutingPolicy(((GPIndividual)ind[1]).trees[0]);

        MultiObjectiveFitness trialFit = (MultiObjectiveFitness)ind[0].fitness.clone();

        int numProcesses = 0;
        for (InstanceSamples iSamples : evaluationModel.getInstanceSamples()) {
            chromosome.split(iSamples.getBaseInstance());

            for (long seed : iSamples.getSeeds()) {
                ProreativeDecisionProcess dp =
                        DecisionProcess.initProreactive(iSamples.getBaseInstance(), seed,
                                policy, chromosome.getSolution());

                numProcesses ++;

                dp.run();

                for (int j = 0; j < trialFit.objectives.length; j++) {
                    Objective objective = evaluationModel.getObjectives().get(j);
                    double objValue = dp.getState().getSolution().objValue(objective);
                    trialFit.objectives[j] += objValue;
                }
            }
        }

        for (int j = 0; j < trialFit.objectives.length; j++)
            trialFit.objectives[j] /= numProcesses;

        // update the fitness of the evaluated individuals
        for (int i = 0; i < ind.length; i++) {
            if (updateFitness[i])
                ((MultiObjectiveFitness)(ind[i].fitness)).setObjectives(state, trialFit.objectives);
        }

        SoPoCEvolutionState sopocState = (SoPoCEvolutionState)state;

        // update the context vector if the fitness is better
        if (trialFit.betterThan(sopocState.getContextFitness())) {
            sopocState.setContextFitness(trialFit);

            for (int i = 0; i < ind.length; i++) {
                sopocState.setContext(i, ind[i]);
            }
        }
    }
}
