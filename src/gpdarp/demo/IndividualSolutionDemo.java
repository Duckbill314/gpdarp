package gpdarp.demo;

import gpdarp.core.*;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndividualSolutionDemo {
    public static void main(String[] args) {
        Path root = FileSystems.getDefault().getPath("").toAbsolutePath();

        List<Integer> travelTimes = new ArrayList<>();
        List<Integer> penalties = new ArrayList<>();

        // read a solution file
        File sol = new File(String.format("%s/test/gp-%d.sol", root, 0));
        String line;
        String[] segments;

        try (BufferedReader reader = new BufferedReader(new FileReader(sol))) {
            // identify the test instance used for this solution
            line = reader.readLine();
            segments = line.split("\\s+");
            int numVehicles = Integer.parseInt(segments[0]);
            String name = segments[2];

            // initialise the associated instance
            File inst = new File(String.format("%s/data/test/%s.txt", root, name));
            Instance instance = Instance.readFromFile(inst);

            assert instance != null;
            List<Request> instanceRequests = instance.getRequests();
            List<Station> instanceStations = instance.getStations();
            List<Vehicle> instanceVehicles = instance.getVehicles();

            File dataFile = new File(root + "/indsolutiondata.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile.getAbsoluteFile()))) {
                writer.write(String.format("test instance %s", name));
                writer.newLine();
                writer.newLine();

                for (int vehicle = 0; vehicle < numVehicles; vehicle++) {
                    int travelTime = 0;
                    double penalty = 0;

                    writer.write(String.format("Route %d", vehicle+1));
                    writer.newLine();

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
                        int time = instance.calculateTravelTime(dist);
                        travelTime += time;

                        writer.write(String.format("0 -> %d = %s -> %s = %d; total = %d",
                                first, start, pickup, time, travelTime));
                        writer.newLine();

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
                            time = instance.calculateTravelTime(dist);
                            travelTime += time;

                            writer.write(String.format("%d -> %d = %s -> %s = %d; total = %d",
                                    id1, id2,
                                    pairedNodes.get(0), pairedNodes.get(1),
                                    time, travelTime));
                            writer.newLine();
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
                                    penalty += instance.getLatenessPenalty() * lateness;
                                }
                            }
                        }
                    }

                    travelTimes.add(travelTime);
                    penalties.add((int) penalty);

                    writer.newLine();
                }

                StringBuilder totalString = new StringBuilder(String.format("total travel duration = %d", travelTimes.getFirst()));
                travelTimes.stream()
                        .skip(1)
                                .forEach(t -> totalString.append(String.format(" + %d", t)));

                int totalTime = travelTimes.stream()
                        .reduce(0, Integer::sum);

                totalString.append(String.format(" = %d", totalTime));
                writer.write(totalString.toString());
                writer.newLine();

                int totalPenalty = penalties.stream()
                        .reduce(0, Integer::sum);
                writer.write(String.format("penalty = %d", totalPenalty));
                writer.newLine();

                writer.write(String.format("total objective value = %d + %d = %d",
                        totalTime,
                        totalPenalty,
                        totalTime + totalPenalty));

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
