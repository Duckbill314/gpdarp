package gpdarp.representation.route;

import gpdarp.core.*;
import gpdarp.decisionprocess.DecisionProcessState;

import java.util.ArrayList;
import java.util.List;

/**
 * A route is ostensibly a collection of arcs - nothing more, nothing less.
 * The main information it maintains is the collection of arcs, and implicitly, the cost of the route.
 * Other information such as position, capacity and demand are handled directly by the Vehicle class.
 *
 * @author William Huang
 */
public class Route {
    private List<Arc> arcs;
    private EphemeralRoute ephemeralRoute;

    public Route(List<Arc> arcs) { this.arcs = arcs; }

    // Initialisation constructor
    public Route() { this(new ArrayList<>()); }

    // Getters
    public List<Arc> getArcs() { return arcs; }
    public EphemeralRoute getEphemeralRoute() { return ephemeralRoute; }
    public Node getEndpoint() { return arcs.getLast().to(); }

    // Setters
    public void setArcs(List<Arc> arcs) { this.arcs = arcs; }
    public void setEphemeralRoute(EphemeralRoute ephemeralRoute) { this.ephemeralRoute = ephemeralRoute; }

    // Manipulators
    public void push(Arc arc) { arcs.add(arc); }
    public Arc pop() {
        if (arcs.isEmpty()) {
            return null;
        }
        return arcs.removeFirst();
    }

    /**
     * Build a route from a list of nodes by converting the node list to an arc list.
     * Used for dynamic route recalculation.
     *
     * @param nodes the list of nodes.
     * @param requests a list of the requests associated with the node list.
     * @return the corresponding route.
     */
    public static Route buildFromNodeList(List<Node> nodes, List<Request> requests) {
        List<Node> clonedNodes = Node.listClone(nodes);
        List<Request> clonedRequests = Request.listClone(requests);

        clonedNodes.stream()
                .filter(node -> node.getRequest() != null)
                .forEach(node -> {
                    Request clonedRequest = clonedRequests.stream()
                            .filter(r -> r.getId() == node.getRequest().getId())
                            .findFirst()
                            .orElse(null);

                    if (clonedRequest != null) {
                        node.setRequest(clonedRequest);
                        switch (node.getType()) {
                            case PICKUP -> clonedRequest.setPickup(node);

                            case DROPOFF -> clonedRequest.setDropoff(node);
                        }
                    }
                });

        List<Arc> arcs = new ArrayList<>();
        List<Arc> clonedArcs = new ArrayList<>();

        for (int i = 0; i < nodes.size()-1; i++) {
            arcs.add(new Arc(nodes.get(i), nodes.get(i+1)));
            clonedArcs.add(new Arc(clonedNodes.get(i), clonedNodes.get(i+1)));
        }

        Route route = new Route(arcs);
        EphemeralRoute ephemeralClone = new EphemeralRoute(clonedArcs, clonedRequests);
        route.setEphemeralRoute(ephemeralClone);

        return route;
    }

    /**
     * Updates the estimated arrival times for all nodes in the route.
     * Applies only to stationary vehicles.
     * The route is set to begin from the current position,
     * at either the current state's time or the vehicle's next available time (whichever comes later).
     *
     * @param state the state of the decision process.
     * @param vehicle the vehicle associated with the route.
     */
    public void updateTimes(DecisionProcessState state, Vehicle vehicle) {
        Instance instance = state.getInstance();

        Arc arc = arcs.getFirst();
        Node from = arc.from();
        Node to = arc.to();

        int serveTime = vehicle.getServeTime();
        int departureTime = Math.max(state.getTime(), from.getDepartureTime());

        from.setDepartureTime(departureTime);
        to.setArrivalTime(departureTime + instance.calculateTravelTime(arc.length()));

        for (int i = 1; i < arcs.size(); i++) {
            arc = arcs.get(i);
            from = arc.from();
            to = arc.to();

            int arrivalTime = from.getArrivalTime();

            if (from.getType() == Node.NodeType.PICKUP) {
                int pickupTime = from.getRequest().getTEarly();
                if (arrivalTime < pickupTime) {
                    arrivalTime = pickupTime;
                }
            }

            departureTime = arrivalTime + serveTime;

            from.setDepartureTime(departureTime);
            to.setArrivalTime(departureTime + instance.calculateTravelTime(arc.length()));
        }

        getEndpoint().setDepartureTime(getEndpoint().getArrivalTime() + vehicle.getServeTime());
    }

    /**
     * The penalty is the sum of all the time that a vehicle was late to pick up a request.
     *
     * @return the penalty.
     */
    public int calculatePenalty() {
        return arcs.stream()
                .map(Arc::from)
                .filter(n -> n.getType() == Node.NodeType.PICKUP)
                .map(n -> n.getArrivalTime() - n.getRequest().getTLate())
                .filter(t -> t > 0)
                .reduce(0, Integer::sum);
    }

    /**
     * Calculate the total time taken to drive along the route.
     *
     * @return the total time.
     */
    public int getTime() {
        return arcs.stream()
                .map(a -> a.to().getArrivalTime() - a.from().getDepartureTime())
                .reduce(0, Integer::sum);
    }

    /**
     * Calculate the sum of the lengths of all arcs in the route.
     *
     * @return the total length.
     */
    public int getLength() {
        return arcs.stream()
                .map(Arc::length)
                .reduce(0, Integer::sum);
    }

    /**
     * Resets the route by replacing the arc list with an empty list.
     */
    public void reset() { arcs = new ArrayList<>(); }

    @Override
    public Route clone() { return new Route(Arc.listClone(arcs)); }

    public boolean isEmpty() { return arcs.isEmpty(); }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Empty route";
        }
        StringBuilder str = new StringBuilder();
        str.append("{");
        str.append(arcs.getFirst().from());
        for (Arc arc : arcs) {
            str.append(String.format(", %s", arc.to()));
        }
        str.append("}");
        return str.toString();
    }
}
