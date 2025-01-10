package gpdarp.demo;

import gpdarp.core.Instance;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.allocationpolicy.requestpolicy.GPRequestPolicy;
import gpdarp.decisionprocess.allocationpolicy.vehiclepolicy.GPVehiclePolicy;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;
import gpdarp.gp.CalcPriorityProblem;
import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.gp.terminal.feature.*;
import gpdarp.representation.route.Route;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstrate the terminal (FeatureGPNode) outputs.
 *
 * @author William Huang
 */
public class GPTerminalDemo {
    public static void main(String[] args) {
        // read an instance from a data file
        Path root = FileSystems.getDefault().getPath("").toAbsolutePath();
        File file = new File(root + "/src/data/sample.txt");
        Instance instance = Instance.readFromFile(file);
        Instance original = Instance.readFromFile(file);
        assert instance != null;
        instance.setOriginalCopy(original);

        // specify a vehicle allocation policy
        VehiclePolicy vehiclePolicy = new GPVehiclePolicy(null);
        RequestPolicy requestPolicy = new GPRequestPolicy(null);

        // initialise a reactive decision process
        ReactiveDecisionProcess rdp = DecisionProcess.initReactive(instance, vehiclePolicy, requestPolicy);
        DecisionProcessState state = rdp.getState();
        for (int i = 1; i < instance.getRequests().size(); i++) {
            rdp.addWaiting(instance.getRequests().get(i));
        }

        // initialise terminal outputs
        List<FeatureGPNode> terminals = new ArrayList<>(Arrays.asList(
                new TimeToPickup(),
                new ExpectedCost(),
                new RequestDemand(),
                new RequestDuration(),
                new ExpectedSlack(),
                new RequestCrowdedness(),
                new VehicleCapacity(),
                new VehicleCharge(),
                new VehicleSlack(),
                new TimeToStation(),
                new OtherBestVehicle()
        ));

        // test the terminal outputs
        Request request = instance.getRequests().getFirst();
        List<Pair<Vehicle, Route>> pool = vehiclePolicy.getPoolFilter().filterVehicles(state, request);
        Pair<Vehicle, Route> candidate = pool.getFirst();
        CalcPriorityProblem calcPrioProblem = new CalcPriorityProblem(candidate.getKey(), request, candidate.getValue().getEphemeralRoute(), state);
        for (FeatureGPNode terminal : terminals) {
            System.out.printf("%s: %f%n", terminal, terminal.value(calcPrioProblem));
        }
    }
}
