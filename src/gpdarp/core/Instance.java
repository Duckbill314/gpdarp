package gpdarp.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * An instance of the dial-a-ride problem.
 * Acts as a focal point of the simulation by storing all the relevant information.
 * At any given time, an instance is akin to a snapshot of the current state of the problem model.
 *
 * @author William Huang
 */
public final class Instance {
    private final String name;
    private List<Vehicle> vehicles;
    private List<Station> stations;
    private List<Request> requests;
    private double timeHorizon;
    private double travelTimeRate;
    private double latenessPenalty;

    // Pseudo-singleton
    private Instance originalCopy;

    public Instance(String name, List<Vehicle> vehicles, List<Station> stations, List<Request> requests,
                    double timeHorizon, double travelTimeRate, double latenessPenalty) {
        this.name = name;
        this.vehicles = vehicles;
        this.stations = stations;
        this.requests = requests;
        this.timeHorizon = timeHorizon;
        this.travelTimeRate = travelTimeRate;
        this.latenessPenalty = latenessPenalty;
    }

    // Getters
    public String getName() { return name; }
    public List<Vehicle> getVehicles() { return vehicles; }
    public List<Station> getStations() { return stations; }
    public List<Request> getRequests() { return requests; }
    public double getTimeHorizon() { return timeHorizon; }
    public double getTravelTimeRate() { return travelTimeRate; }
    public double getLatenessPenalty() { return latenessPenalty; }
    public int getNumVehicles() { return vehicles.size(); }
    public int getNumStations() { return stations.size(); }
    public int getNumRequests() { return requests.size(); }

    // Setters
    public void setOriginalCopy(Instance originalCopy) { this.originalCopy = originalCopy; }

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
        List<Vehicle> vehicles = new ArrayList<>();
        List<Station> stations = new ArrayList<>();
        List<Request> requests = new ArrayList<>();
        double timeHorizon;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // line 1: "n_K n n_S T"
            line = reader.readLine(); // line 2: corresponding entries for line 1
            segments = line.split("\\s+");
            int numVehicles = Integer.parseInt(segments[0]);
            int numRequests = Integer.parseInt(segments[1]);
            int numStations = Integer.parseInt(segments[2]);
            timeHorizon = Double.parseDouble(segments[3]);

            reader.readLine(); // line 3: "C Q β α d_serv ϕ ρ"
            line = reader.readLine(); // line 4: corresponding entries for line 3
            segments = line.split("\\s+");
            int capacity = Integer.parseInt(segments[0]);
            double chargeMax = Double.parseDouble(segments[1]);
            double chargeDepletionRate = Double.parseDouble(segments[2]);
            double chargeFillRate = Double.parseDouble(segments[3]);
            int serveTime = Integer.parseInt(segments[4]);
            double travelTimeRate = Double.parseDouble(segments[5]);
            double latenessPenalty = Double.parseDouble(segments[6]);

            reader.readLine(); // line 5: "B0[1] ... B0[n_K]"
            line = reader.readLine(); // line 6: corresponding entries for line 5
            segments = line.split("\\s+");
            List<Double> chargeStates = new ArrayList<>();
            for (String segment : segments) {
                chargeStates.add(Double.parseDouble(segment));
            }

            reader.readLine(); // line 7: "n_exp[1] ... n_exp[T/60]xx"
            reader.readLine(); // line 8: corresponding entries for line 7

            reader.readLine(); // line 9: "loc_S[1] ... loc_S[n_S]"
            line = reader.readLine(); // line 10: corresponding entries for line 9
            segments = line.split("\\s+");
            for (int i = 0; i < segments.length; i += 2) {
                int x = Integer.parseInt(segments[i]);
                int y = Integer.parseInt(segments[i + 1]);
                stations.add(new Station(x, y));
            }

            reader.readLine(); // line 11: "loc_K[1] ... loc_K[n_K]"
            line = reader.readLine(); // line 12: corresponding entries for line 11
            segments = line.split("\\s+");
            List<Node> vehicleNodes = new ArrayList<>();
            for (int i = 0; i < segments.length; i += 2) {
                int x = Integer.parseInt(segments[i]);
                int y = Integer.parseInt(segments[i + 1]);
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
                int tRec = Integer.parseInt(segments[1]);
                int x = Integer.parseInt(segments[2]);
                int y = Integer.parseInt(segments[3]);
                Node pickup = new Node(x, y);
                x = Integer.parseInt(segments[4]);
                y = Integer.parseInt(segments[5]);
                Node dropoff = new Node(x, y);
                int tEarly = Integer.parseInt(segments[6]);
                int tLate = Integer.parseInt(segments[7]);
                int tMax = Integer.parseInt(segments[8]);
                // TODO: different demand amounts
                requests.add(new Request(id, tRec, pickup, dropoff, tEarly, tLate, tMax, 1));
                line = reader.readLine();
            }
            // TODO: distance travel rate
            return new Instance(file.getName(), vehicles, stations, requests, timeHorizon, 10000 * travelTimeRate,
                    latenessPenalty);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find the closest station from any reference point.
     *
     * @param pos the node of the reference point.
     * @return the closest station.
     */
    public Station findClosestStation(Node pos) {
        return stations.stream()
                .min(Comparator.comparingDouble(pos::calcDist))
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Helper method that converts distance into travel time.
     *
     * @param distance the given distance.
     * @return the calculated travel time.
     */
    public int calculateTravelTime(int distance) { return (int) Math.ceil(distance / travelTimeRate); }

    @Override
    public String toString() {
        return String.format("Instance with %d vehicles, %d stations, and %d getRequests",
                getNumVehicles(), getNumStations(), getNumRequests());
    }

    @Override
    public Instance clone() {
        Instance clone = new Instance(
                name,
                Vehicle.listClone(vehicles),
                Node.listClone(stations),
                Request.listClone(requests),
                timeHorizon,
                travelTimeRate,
                latenessPenalty);
        clone.setOriginalCopy(originalCopy);
        return clone;
    }

    /**
     * Resets all values by replacing them with deep clones from a pseudo-singleton copy of the original Instance.
     */
    public void reset() {
        this.vehicles = Vehicle.listClone(originalCopy.vehicles);
        this.stations = Node.listClone(originalCopy.stations);
        this.requests = Request.listClone(originalCopy.requests);
        this.timeHorizon = originalCopy.timeHorizon;
        this.travelTimeRate = originalCopy.travelTimeRate;
        this.latenessPenalty = originalCopy.latenessPenalty;
    }
}
