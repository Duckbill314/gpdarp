package gpdarp.gp;

import gputils.function.*;
import gpdarp.gp.terminal.feature.*;
import gputils.terminal.PrimitiveSet;

/**
 * The primitive set for UCARP.
 */

public class UCARPPrimitiveSet extends PrimitiveSet {
    /**
     * Return the basic terminals:
     *  - ServeCost
     *  - CostFromDepot
     *  - CostFromHere
     *  - CostToDepot
     *  - CostRefill
     *  - DeadheadingCost
     *  - Demand
     *  - RemainingCapacity
     *  - Fullness
     *  - FractionRemainingTasks
     *  - FractionUnassignedTasks
     * @return the basic terminal set.
     */
    public static UCARPPrimitiveSet basicTerminalSet() {
        UCARPPrimitiveSet terminalSet = new UCARPPrimitiveSet();
//        terminalSet.add(new FloodFill());

        return terminalSet;
    }

    /**
     * The extended terminal set includes the basic terminals as well as
     * the extended ones.
     * @return the extended terminal set.
     */
    public static UCARPPrimitiveSet extendedTerminalSet() {
        UCARPPrimitiveSet terminalSet = basicTerminalSet();

        return terminalSet;
    }

    /**
     * The terminal set used for generating routes sequentially.
     * No need to consider other routes.
     * @return the terminal set.
     */
    public static UCARPPrimitiveSet seqTerminalSet() {
        UCARPPrimitiveSet terminalSet = basicTerminalSet();

        return terminalSet;
    }

    /**
     * The whole terminal set including all the possible terminals.
     * It is the extended terminal set in this case.
     * @return the whole terminal set.
     */
    public static UCARPPrimitiveSet wholeTerminalSet() {
        return extendedTerminalSet();
    }

    /**
     * The whole primitive set includes the whole terminal set
     * and all the function nodes.
     * @return the whole primitive set.
     */
    public static UCARPPrimitiveSet wholePrimitiveSet() {
        UCARPPrimitiveSet primitiveSet = wholeTerminalSet();

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
