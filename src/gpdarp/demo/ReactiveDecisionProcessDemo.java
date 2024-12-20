package gpdarp.demo;

import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.core.Instance;
import gpdarp.decisionprocess.allocationpolicy.requestpolicy.NearestRequestPolicy;
import gpdarp.decisionprocess.allocationpolicy.vehiclepolicy.NearestVehiclePolicy;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;
import util.Timer;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
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
        double duration = (end - start) / 1000000;

        System.out.println(rdp.getState().getSolution().toString());
        System.out.println("Cost:");
        System.out.println(rdp.getState().getSolution().totalCost());
        System.out.println("elapsed " + duration + " ms. \n");

        // rerun the decision process for a number of times.
        // the instance and routing policy do not change,
        // so all the reruns will give the same results.
        int maxReruns = 2;
        for (int rerun = 0; rerun < maxReruns; rerun++) {
            // before rerunning, need to reset the decision process
            rdp.reset();
            start = Timer.getCpuTime();
            rdp.run();
            end = Timer.getCpuTime();
            duration = (end - start) / 1000000;

            System.out.println(rdp.getState().getSolution().toString());
            System.out.println("Cost:");
            System.out.println(rdp.getState().getSolution().totalCost());
            System.out.println("elapsed " + duration + " ms. \n");
        }


    }
}
