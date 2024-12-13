package gpdarp.representation;

import gpdarp.core.Vehicle;
import gpdarp.representation.route.Route;

import java.util.HashMap;
import java.util.Map;

public class Pool extends HashMap<Vehicle, Route> {
    @Override
    public Pool clone() {
        Pool clonedPool = new Pool();
        for (Map.Entry<Vehicle, Route> entry : this.entrySet()) {
            clonedPool.put(entry.getKey().clone(), entry.getValue().clone());
        }
        return clonedPool;
    }
}
