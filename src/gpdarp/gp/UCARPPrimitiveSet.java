package gpdarp.gp;

import gpdarp.gp.terminal.FeatureGPNode;
import gpdarp.gp.terminal.feature.*;
import gputils.function.*;
import gputils.terminal.PrimitiveSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The primitive set for GP hyper heuristic dial-a-ride-problem.
 *
 * @author William Huang
 */
public class UCARPPrimitiveSet extends PrimitiveSet {
    /**
     * The terminal set includes:
     *  - Travel time to pickup point,
     *  - Expected request cost,
     *  - Request demand,
     *  - Request duration,
     *  - Expected request slack,
     *  - Request crowdedness,
     *  - Vehicle remaining capacity,
     *  - Vehicle remaining charge,
     *  - Vehicle minimum slack,
     *  - Travel time to charging station,
     *  - Travel time of other best vehicle to serve the request.
     *
     * @return the terminal set.
     */
    public static UCARPPrimitiveSet terminalSet() {
        UCARPPrimitiveSet terminalSet = new UCARPPrimitiveSet();

        List<FeatureGPNode> terminals = new ArrayList<>(Arrays.asList(
                new TimeToPickup(),
                new ExpectedCost(),
                new RequestDemand(),
                new RequestDuration(),
                new ExpectedSlack(),
                new RequestCrowdedness(),
                new VehicleCapacity(),
                new VehicleCharge(),
                new VehicleSlack(),
                new TimeToStation(),
                new OtherBestVehicle()
        ));

        for (FeatureGPNode terminal : terminals) {
            terminalSet.add(terminal);
        }

        return terminalSet;
    }

    /**
     * The primitive set includes the terminal set and all the basic function nodes.
     *
     * @return the primitive set.
     */
    public static UCARPPrimitiveSet primitiveSet() {
        UCARPPrimitiveSet primitiveSet = terminalSet();

        primitiveSet.add(new Add());
        primitiveSet.add(new Sub());
        primitiveSet.add(new Mul());
        primitiveSet.add(new Div());
        primitiveSet.add(new Max());
        primitiveSet.add(new Min());
        primitiveSet.add(new If());

        return primitiveSet;
    }

}
