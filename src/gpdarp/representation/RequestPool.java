package gpdarp.representation;

import gpdarp.core.WaitingRequest;
import gpdarp.representation.route.Route;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the pool of potential candidate requests to be allocated to a singular vehicle,
 * as well as the corresponding routes.
 *
 * @author William Huang
 */
public class RequestPool extends HashMap<WaitingRequest, Route> {
    @Override
    public RequestPool clone() {
        RequestPool clonedRequestPool = new RequestPool();
        for (Map.Entry<WaitingRequest, Route> entry : this.entrySet()) {
            clonedRequestPool.put(entry.getKey().clone(), entry.getValue().clone());
        }
        return clonedRequestPool;
    }
}
