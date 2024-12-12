package gpdarp.decisionprocess.poolfilter;

import gpdarp.core.Instance;
import gpdarp.core.Node;
import gpdarp.core.Request;
import gpdarp.core.Vehicle;
import gpdarp.decisionprocess.DecisionProcessState;
import gpdarp.decisionprocess.PoolFilter;
import gpdarp.representation.route.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Filters for the candidate vehicles from the pool by selecting only the vehicles that are expected to be feasible,
 * i.e. the vehicle meets the following conditions:
 * - it has remaining capacity,
 * - there exists a feasible planned route for the vehicle,
 * - after completing its planned route, it will still have enough charge to reach the nearest charging station.
 *
 * @author William Huang
 */
public class FeasiblePoolFilter extends PoolFilter {
    @Override
    public List<Vehicle> filter(Request request, DecisionProcessState state) {
        Instance instance = state.getInstance();
        List<Vehicle> filtered = new ArrayList<>(instance.getVehicles());
        filtered.removeIf(v -> v.getRemainingCapacity() == 0);

        for (Vehicle vehicle : filtered) {
            List<Request> requests = new ArrayList<>(vehicle.getRequests());
            requests.add(request);
            Route route = vehicle.recalculate(state, requests);
            if (route == null) {
                filtered.remove(vehicle);
            }
            else {
                int routeLength = route.getLength();
                Node endpoint = route.getEndpoint();
                int returnLength = endpoint.calcDist(instance.findClosestStation(endpoint));
                double estimatedChargeState = vehicle.estimateDepletion(routeLength + returnLength);
                if (estimatedChargeState < 0) {
                    filtered.remove(vehicle);
                }
            }
        }
        return filtered;
    }
}
