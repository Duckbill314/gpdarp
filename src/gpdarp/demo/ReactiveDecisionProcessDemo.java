package gpdarp.demo;

import gpdarp.core.Request;
import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.core.Instance;
import gpdarp.decisionprocess.allocationpolicy.requestpolicy.NearestRequestPolicy;
import gpdarp.decisionprocess.allocationpolicy.vehiclepolicy.NearestVehiclePolicy;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;
import gpdarp.representation.Solution;
import gpdarp.representation.route.Route;
import util.Timer;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

/**
 * A demo for a reactive decision process.
 * First, an instances is read from a data file, e.g. data/training/1.txt.
 * Then, given a vehicle allocation policy and a request allocation policy, the decision process is constructed
 * and executed.
 *
 * @author gphhucarp, William Huang
 */
public class ReactiveDecisionProcessDemo {

    public static void main(String[] args) {
        // read an instance from a data file
        Path root = FileSystems.getDefault().getPath("").toAbsolutePath();
        File file = new File(root + "/src/data/sample.txt");
        Instance instance = Instance.readFromFile(file);
        Instance original = Instance.readFromFile(file);
        instance.setOriginalCopy(original);

        // specify a vehicle allocation policy
        VehiclePolicy vehiclePolicy = new NearestVehiclePolicy();
        RequestPolicy requestPolicy = new NearestRequestPolicy();

        // initialise a reactive decision process
        ReactiveDecisionProcess rdp = DecisionProcess.initReactive(instance, vehiclePolicy, requestPolicy);

        // run the decision process
        long start = Timer.getCpuTime();
        rdp.run();
        long end = Timer.getCpuTime();
        double duration = (double) (end - start) / 1000000;

        System.out.println(rdp.getState().getSolution().toString());
        for (Request request : rdp.getState().getInstance().getRequests()) {
            System.out.println(request);
        }
        rdp.getState().getSolution().totalCost();
        System.out.println("elapsed " + duration + " ms. \n");

        Solution solution = rdp.getState().getSolution();
        List<Route> routes = solution.getRoutes();
        for (Route route : routes) {
            System.out.println(route);
            route.getArcs().stream()
                    .map(a -> a.to().getArrivalTime() - a.from().getDepartureTime())
                    .forEach(t -> System.out.println(t));
        }

        // rerun the decision process for a number of times.
        // the instance and routing policy do not change,
        // so all the reruns will give the same results.
        int maxReruns = 0;
        for (int rerun = 0; rerun < maxReruns; rerun++) {
            // before rerunning, need to reset the decision process
            rdp.reset();
            start = Timer.getCpuTime();
            rdp.run();
            end = Timer.getCpuTime();
            duration = (double) (end - start) / 1000000;

            System.out.println(rdp.getState().getSolution().toString());
            for (Request request : rdp.getState().getInstance().getRequests()) {
                System.out.println(request);
            }
            rdp.getState().getSolution().totalCost();
            System.out.println("elapsed " + duration + " ms. \n");
        }


    }
}
