package gpdarp.representation;

import gpdarp.core.Vehicle;
import gpdarp.representation.route.Route;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the pool of potential candidate vehicles to accept a request, as well as the corresponding routes.
 *
 * @author William Huang
 */
public class VehiclePool extends HashMap<Vehicle, Route> {
    @Override
    public VehiclePool clone() {
        VehiclePool clonedVehiclePool = new VehiclePool();
        for (Map.Entry<Vehicle, Route> entry : this.entrySet()) {
            clonedVehiclePool.put(entry.getKey().clone(), entry.getValue().clone());
        }
        return clonedVehiclePool;
    }
}
