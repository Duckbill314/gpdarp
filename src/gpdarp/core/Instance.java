package gpdarp.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * An instance of the dial-a-ride problem.
 *
 * @author William Huang
 */
public record Instance(List<Vehicle> vehicles, List<Station> stations, List<Request> requests, double timeHorizon,
                       List<Double> expectation) {

    public int getNumVehicles() { return vehicles.size(); }
    public int getNumStations() { return stations.size(); }
    public int getNumRequests() { return requests.size(); }

    /**
     * Parse a data file to initialise an Instance.
     *
     * @param file the data file.
     * @return the instance.
     */
    public static Instance readFromFile(File file) {
        // Parsing tools
        String line;
        String[] segments;

        // Instance fields
        double timeHorizon;
        List<Double> expectation = new ArrayList<Double>();
        List<Vehicle> vehicles = new ArrayList<>();
        List<Station> stations = new ArrayList<>();
        List<Request> requests = new ArrayList<Request>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // line 1: "n_K n n_S T"
            line = reader.readLine(); // line 2: corresponding entries for line 1
            segments = line.split("\\s+");
            int numVehicles = Integer.parseInt(segments[0]);
            int numRequests = Integer.parseInt(segments[1]);
            int numStations = Integer.parseInt(segments[2]);
            timeHorizon = Double.parseDouble(segments[3]);

            reader.readLine(); // line 3: "C Q β α d_serv"
            line = reader.readLine(); // line 4: corresponding entries for line 3
            segments = line.split("\\s+");
            int capacity = Integer.parseInt(segments[0]);
            double chargeMax = Double.parseDouble(segments[1]);
            double chargeDepletionRate = Double.parseDouble(segments[2]);
            double chargeFillRate = Double.parseDouble(segments[3]);
            double serveTime = Double.parseDouble(segments[4]);

            reader.readLine(); // line 5: "B0[1] ... B0[n_K]"
            line = reader.readLine(); // line 6: corresponding entries for line 5
            segments = line.split("\\s+");
            List<Double> chargeStates = new ArrayList<Double>();
            for (String segment : segments) {
                chargeStates.add(Double.parseDouble(segment));
            }

            reader.readLine(); // line 7: "n_exp[1] ... n_exp[T/60]xx"
            line = reader.readLine(); // line 8: corresponding entries for line 7
            segments = line.split("\\s+");
            for (String segment : segments) {
                expectation.add(Double.parseDouble(segment));
            }

            reader.readLine(); // line 9: "loc_S[1] ... loc_S[n_S]"
            line = reader.readLine(); // line 10: corresponding entries for line 9
            segments = line.split("\\s+");
            for (int i = 0; i < segments.length; i += 2) {
                double x = Double.parseDouble(segments[i]);
                double y = Double.parseDouble(segments[i + 1]);
                stations.add(new Station(x, y));
            }

            reader.readLine(); // line 11: "loc_K[1] ... loc_K[n_K]"
            line = reader.readLine(); // line 12: corresponding entries for line 11
            segments = line.split("\\s+");
            List<Node> vehicleNodes = new ArrayList<Node>();
            for (int i = 0; i < segments.length; i += 2) {
                double x = Double.parseDouble(segments[i]);
                double y = Double.parseDouble(segments[i + 1]);
                vehicleNodes.add(new Node(x, y));
            }

            // Aggregating vehicle information to construct the objects
            for (int i = 0; i < numVehicles; i++) {
                vehicles.add(new Vehicle(i + 1, capacity, chargeMax, chargeStates.get(i), chargeFillRate,
                        chargeDepletionRate, serveTime, vehicleNodes.get(i)));
            }

            reader.readLine(); // line 13: "Requests: id t_arr u_x u_y v_x v_y t_start t_end d_max"
            line = reader.readLine(); // line 14+: corresponding entries for line 12
            while (line != null) {
                segments = line.split("\\s+");
                int id = Integer.parseInt(segments[0]);
                float tRec = Float.parseFloat(segments[1]);
                double x = Double.parseDouble(segments[2]);
                double y = Double.parseDouble(segments[3]);
                Node pickup = new Node(x, y);
                x = Double.parseDouble(segments[4]);
                y = Double.parseDouble(segments[5]);
                Node dropoff = new Node(x, y);
                float tEarly = Float.parseFloat(segments[6]);
                float tLate = Float.parseFloat(segments[7]);
                float tMax = Float.parseFloat(segments[8]);
                requests.add(new Request(id, tRec, pickup, dropoff, tEarly, tLate, tMax));
            }

            return new Instance(vehicles, stations, requests, timeHorizon, expectation);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return String.format("Instance with %d vehicles, %d stations, and %d requests",
                getNumVehicles(), getNumStations(), getNumRequests()); }

    @Override
    public Instance clone() throws CloneNotSupportedException { return (Instance) super.clone(); }
}
