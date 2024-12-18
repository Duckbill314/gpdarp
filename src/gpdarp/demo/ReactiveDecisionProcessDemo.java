package gpdarp.demo;

import gpdarp.decisionprocess.DecisionProcess;
import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.core.Instance;
import gpdarp.decisionprocess.reactive.ReactiveDecisionProcess;
import util.Timer;

import java.io.File;
// TODO: modify here and try it out for yourself!
/**
 * A demo for a reactive decision process.
 * First, an instances is read from a data file, e.g. data/gdb/gdb23.dat.
 * Then, given a routing policy
 * Created by gphhucarp on 29/08/17.
 */
public class ReactiveDecisionProcessDemo {

    public static void main(String[] args) {
        long seed = 0;
        double demULevel = 0.3;
        double costULevel = 0.2;

        // read an instance from a data file
        Instance instance = Instance.readFromFile(new File("")); // TODO: declare data file

        // specify a vehicle allocation policy
        VehiclePolicy vehiclePolicy = null; // TODO: declare policy
        RequestPolicy requestPolicy = null; // TODO: declare policy

        // initialise a reactive decision process
        ReactiveDecisionProcess rdp = DecisionProcess.initReactive(instance, vehiclePolicy, requestPolicy);

        // run the decision process
        // these should give the same results
        long start = Timer.getCpuTime();
        rdp.run();
        long end = Timer.getCpuTime();
        double duration = (end - start) / 1000000;

        System.out.println(rdp.getState().getSolution().toString());
        System.out.println(rdp.getState().getSolution().totalCost());
        System.out.println("elapsed " + duration + " ms.");

        // rerun the decision process for a number of times.
        // the instance and routing policy do not change,
        // so all the reruns will give the same results.
        int maxReruns = 10;
        for (int rerun = 0; rerun < maxReruns; rerun++) {
            // before rerunning, need to reset the decision process
            rdp.reset();
            start = Timer.getCpuTime();
            rdp.run();
            end = Timer.getCpuTime();
            duration = (end - start) / 1000000;

            System.out.println(rdp.getState().getSolution().toString());
            System.out.println(rdp.getState().getSolution().totalCost());
            System.out.println("elapsed " + duration + " ms.");
        }
    }
}
