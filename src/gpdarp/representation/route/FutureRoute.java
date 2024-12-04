package gpdarp.representation.route;

import gpdarp.core.Arc;
import gpdarp.core.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for dynamically planning the best route that a vehicle should take.
 *
 * @author William Huang
 */
public class FutureRoute extends Route {

    public FutureRoute(List<Arc> arcs) { super(arcs); }

    public FutureRoute() { super(); }

    public FutureRoute clone() { return new FutureRoute(this.getArcs()); }

    public void insert(Arc request) {
        List<Arc> arcs = this.getArcs();
        if (arcs.isEmpty()) {
            this.push(request);
        }
        else {
            List<Position> route = new ArrayList<Position>();
            route.add(arcs.getFirst().from());
            for (Arc arc : arcs) {
                route.add(arc.to());

            }
        }

    }
}
