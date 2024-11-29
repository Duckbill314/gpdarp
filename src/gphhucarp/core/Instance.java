package gphhucarp.core;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

 /**
 * A sampled UCARP instance.
 * It includes the random demands and deadheading costs of each arc.
 * In addition, it samples actual demands and deadheading costs of the arcs
 * and store in the corresponding actualDemand and actualDeadheadingCost fields.
 *
 * Created by gphhucarp on 14/06/17.
 */
public class Instance {
    private String name = null; // the name of the instance

    private int numVehicles;

    private Map<Arc, List<Arc>> taskToTaskMap; // the task-to-task map, used for generating features in the decision making process.

    public Instance(List<Arc> tasks, int depot, Arc depotLoop, double capacity, int numVehicles,
                    double demandUncertaintyLevel, double costUncertaintyLevel) {

        calcTaskToTaskMap();
    }

    public Instance(List<Arc> tasks, int depot, double capacity, int numVehicles,
                    double demandUncertaintyLevel, double costUncertaintyLevel) {
        this(tasks, depot,
                new Arc(depot, depot, 0, 0, 0, null, 0, 0),
                capacity, numVehicles, demandUncertaintyLevel, costUncertaintyLevel);
    }

    public int getNumVehicles() {
        return numVehicles;
    }

    public Map<Arc, List<Arc>> getTaskToTaskMap() {
        return taskToTaskMap;
    }

    public String getName() {
         return name;
     }

    public void setName(String name) {
         this.name = name;
     }

    public void calcTaskToTaskMap() {
        taskToTaskMap = new HashMap<>();
        // add the depot loop dummy task
        List<Arc> depotLoopAdjacencyList = new LinkedList<>(tasks);
        Collections.sort(depotLoopAdjacencyList,
                (o1, o2) -> Double.compare(graph.getEstDistance(depotLoop, o1), graph.getEstDistance(depotLoop, o2)));
        taskToTaskMap.put(depotLoop, depotLoopAdjacencyList);

        for (Arc task : tasks) {
            List<Arc> taskAdjacencyList = new LinkedList<>();

            for (Arc anotherTask: tasks) {
                if (anotherTask.equals(task) || anotherTask.equals(task.getInverse()))
                    continue;

                taskAdjacencyList.add(anotherTask);
            }

            Collections.sort(taskAdjacencyList,
                    (o1, o2) -> Double.compare(graph.getEstDistance(task, o1), graph.getEstDistance(task, o2)));
            taskToTaskMap.put(task, taskAdjacencyList);
        }
    }

    /**
     * Read a data file.
     * @param file the data file.
     * @return the instance.
     */
    public static Instance readFromFile(File file) {
        String line;
        String[] segments;

        List<Double> chargeStates = new ArrayList<Double>();
        List<Double> expectation = new ArrayList<Double>();
        List<Position> stationPositions = new ArrayList<Position>();
        List<Position> vehiclePositions = new ArrayList<Position>();

        List<Request> requests = new ArrayList<Request>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // line 1: "n_K n n_S T"
            line = reader.readLine(); // line 2: corresponding entries for line 1
            segments = line.split("\\s+");
            int numVehicles = Integer.parseInt(segments[0]);
            int numRequests = Integer.parseInt(segments[1]);
            int numStations = Integer.parseInt(segments[2]);
            Double timeHorizon = Double.parseDouble(segments[3]);

            reader.readLine(); // line 3: "C Q β α d_serv"
            line = reader.readLine(); // line 4: corresponding entries for line 3
            segments = line.split("\\s+");
            Double capacity = Double.parseDouble(segments[0]);
            Double chargeMax = Double.parseDouble(segments[1]);
            Double chargeDepletionRate = Double.parseDouble(segments[2]);
            Double chargeFillRate = Double.parseDouble(segments[3]);
            Double serveTime = Double.parseDouble(segments[4]);

            reader.readLine(); // line 5: "B0[1] ... B0[n_K]"
            line = reader.readLine(); // line 6: corresponding entries for line 5
            segments = line.split("\\s+");
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
                double y = Double.parseDouble(segments[i+1]);
                stationPositions.add(new Position(x, y));
            }

            reader.readLine(); // line 11: "loc_K[1] ... loc_K[n_K]"
            line = reader.readLine(); // line 12: corresponding entries for line 11
            segments = line.split("\\s+");
            for (int i = 0; i < segments.length; i += 2) {
                double x = Double.parseDouble(segments[i]);
                double y = Double.parseDouble(segments[i+1]);
                vehiclePositions.add(new Position(x, y));
            }

            reader.readLine(); // line 13: "Requests: id t_arr u_x u_y v_x v_y t_start t_end d_max"
            line = reader.readLine(); // line 14+: corresponding entries for line 12
            while (line != null) {
                segments = line.split("\\s+");
                int id = Integer.parseInt(segments[0]);
                float tRec = Float.parseFloat(segments[1]);
                double x = Double.parseDouble(segments[2]);
                double y = Double.parseDouble(segments[3]);

                x = Double.parseDouble(segments[4]);
                y = Double.parseDouble(segments[5]);


            }



            return new Instance();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Calculate the actual distances from a node to all other nodes.
     * This is done by Dijkstra's algorithm.
     * @param node the starting node.
     */
    private void calcActDistancesFrom(int node) {
        List<Integer> nodes = graph.getNodes();

        // local class for search nodes in the priority queue
        class SearchNode {
            private int node;
            private double pathLength;

            private SearchNode(int node, double pathLength) {
                this.node = node;
                this.pathLength = pathLength;
            }
        }

        // the priority queue uses path length as priority, the smaller the better
        // the tie breaker is the node id.
        PriorityQueue<SearchNode> pq =
                new PriorityQueue<>((o1, o2) -> {
                    double lengthDiff = o1.pathLength - o2.pathLength;

                    if (lengthDiff < 0)
                        return -1;
                    if (lengthDiff > 0)
                        return 1;
                    if (o1.node < o2.node)
                        return -1;
                    if (o1.node > o2.node)
                        return 1;
                    return 0;
                });
        pq.add(new SearchNode(node, 0));

        // whether each node is visited or not, initially all false
        boolean[] visited = new boolean[nodes.get(nodes.size()-1)+1];

        while (!pq.isEmpty()) {
            SearchNode next = pq.poll();

            if (visited[next.node])
                continue;

            visited[next.node] = true;
            actDistMatrix[node][next.node] = next.pathLength;

            for (Arc arc : graph.getOutNeighbour(next.node)) {
                int neigh = arc.getTo();
                if (visited[neigh])
                    continue;

                double lengthToNeigh = next.pathLength + actCostMatrix[arc.getFrom()][arc.getTo()];
                pq.add(new SearchNode(neigh, lengthToNeigh));
            }
        }
    }

    @Override
    public String toString() {
        String str = graph.toString();
        str = str + "depot = " + depot + " \n";
        str = str + "capacity = " + capacity + " \n";

        return str;
    }

    public Instance clone() {
        return new Instance(graph, tasks, depot, depotLoop, capacity, numVehicles,
                demandUncertaintyLevel, costUncertaintyLevel);
    }
}
