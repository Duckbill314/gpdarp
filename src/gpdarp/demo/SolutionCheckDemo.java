package gpdarp.demo;

import gpdarp.core.*;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolutionCheckDemo {
    public static int RUNS = 1;
    public static int SOLUTIONS = 500;
    public static String DATASET = "gp";
    public static int REQUESTS = 10000;
    public static int VEHICLES = 400;
    public static String DISTRIBUTION = "uniform";


    public static void main(String[] args) {
        Path root = FileSystems.getDefault().getPath("").toAbsolutePath();

        List<String> instanceNames = new ArrayList<>();
        List<Integer> travelTimes = new ArrayList<>();
        List<Integer> penalties = new ArrayList<>();

        for (int i = 0; i < RUNS; i++) {
            for (int j = 0; j < SOLUTIONS; j++) {
                int travelTime = 0;
                int penalty = 0;

                // read a solution file
                File sol = new File(String.format("%s/test/%s/%d/n%05d-m%04d-%s-test%05d.sol",
                        root, DATASET, i, REQUESTS, VEHICLES, DISTRIBUTION, j+1));
                String line;
                String[] segments;

                try (BufferedReader reader = new BufferedReader(new FileReader(sol))) {
                    // identify the test instance used for this solution
                    line = reader.readLine();
                    segments = line.split("\\s+");
                    int numVehicles = Integer.parseInt(segments[0]);
                    String name = segments[2];
                    instanceNames.add(name);

                    // initialise the associated instance
                    File inst = new File(String.format("%s/data/test/%s.txt", root, name));
                    Instance instance = Instance.readFromFile(inst);

                    assert instance != null;
                    List<Request> instanceRequests = instance.getRequests();
                    List<Station> instanceStations = instance.getStations();
                    List<Vehicle> instanceVehicles = instance.getVehicles();

                    for (int vehicle = 0; vehicle < numVehicles; vehicle++) {
                        // request IDs
                        line = reader.readLine();
                        segments = line.split("\\s+");
                        List<Integer> requests = Arrays.stream(segments)
                                .skip(1)
                                .map(Integer::parseInt)
                                .toList();

                        // arrival and departure times
                        line = reader.readLine();
                        segments = line.split("\\s+");
                        List<Integer> times = Arrays.stream(segments)
                                .skip(1)
                                .map(Integer::parseInt)
                                .toList();

                        // calculate and add travel times
                        if (!requests.isEmpty()) {
                            Node start = instanceVehicles.get(vehicle).getCurrPos();
                            int first = requests.getFirst();
                            Node pickup = instanceRequests.get(first - 1).getPickup();

                            int dist = start.calcDist(pickup);
                            travelTime += instance.calculateTravelTime(dist);

                            for (int ind = 0; ind < requests.size()-1; ind++) {
                                int id1 = requests.get(ind);
                                int id2 = requests.get(ind+1);

                                List<Integer> pairedIds = new ArrayList<>(Arrays.asList(id1, id2));
                                List<Node> pairedNodes = new ArrayList<>();

                                for (int id : pairedIds) {
                                    Node node;
                                    if (id < 1000000) {
                                        if (id > 0) {
                                            node = instanceRequests.get(id - 1).getPickup();
                                        }
                                        else {
                                            node = instanceRequests.get(-1 * id - 1).getDropoff();
                                        }
                                    }
                                    else {
                                        node = instanceStations.get(id - 1000001);
                                    }
                                    pairedNodes.add(node);
                                }

                                dist = pairedNodes.get(0).calcDist(pairedNodes.get(1));
                                travelTime += instance.calculateTravelTime(dist);
                            }
                        }

                        // calculate and add penalties
                        if (!times.isEmpty()) {
                            for (int ind = 0; ind < times.size(); ind++) {
                                int id = requests.get(ind);
                                if (id > 0 && id < 1000000) {
                                    int arrivalTime = times.get(ind) - instanceVehicles.get(vehicle).getServeTime();
                                    Request request = instanceRequests.get(id - 1);
                                    int lateness = arrivalTime - request.getTLate();
                                    if (lateness > 0) {
                                        penalty += (int) (instance.getLatenessPenalty() * lateness);
                                    }
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                travelTimes.add(travelTime);
                penalties.add(penalty);
            }
        }

        File dataFile = new File(root + "/solutiondata.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile.getAbsoluteFile()))) {
            writer.write("run,instance,travelTime,penalty,totalCost");
            writer.newLine();

            for (int i = 0; i < RUNS; i++) {
                for (int j = 0; j < SOLUTIONS; j++) {
                    int ind = i * SOLUTIONS + j;
                    writer.write(String.format("%d,%s,%d,%d,%d",
                            i,
                            instanceNames.get(ind),
                            travelTimes.get(ind),
                            penalties.get(ind),
                            travelTimes.get(ind) + penalties.get(ind)));
                    writer.newLine();
                }
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
