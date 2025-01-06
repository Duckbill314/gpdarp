package gpdarp.representation.route;

import gpdarp.core.Arc;
import gpdarp.core.Request;

import java.util.List;

public class EphemeralRoute extends Route {
    private List<Request> requestClones;

    public EphemeralRoute(List<Arc> arcClones, List<Request> requestClones) {
        super(arcClones);
        this.requestClones = requestClones;
    }

    // Getters
    public List<Request> getRequestClones() { return requestClones; }
}
