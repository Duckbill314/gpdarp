package gpdarp.core;

import gpdarp.decisionprocess.RequestPolicy;
import gpdarp.decisionprocess.VehiclePolicy;
import gpdarp.decisionprocess.allocationpolicy.requestpolicy.NearestRequestPolicy;
import gpdarp.decisionprocess.allocationpolicy.vehiclepolicy.NearestVehiclePolicy;

import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration for all the possible objectives.
 *
 * @author gphhucarp, William Huang
 */
public enum Objective {
    TOTAL_COST("total-cost"),
    MAX_ROUTE_COST("max-route-cost");

    private final String name;

    Objective(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Reverse-lookup map
    private static final Map<String, Objective> lookup = new HashMap<>();

    static {
        for (Objective a : Objective.values()) {
            lookup.put(a.getName(), a);
        }
    }

    public static Objective get(String name) {
        return lookup.get(name);
    }

    /**
     * The reference vehicle allocation policy for calculating the reference objective.
     *
     * @return the reference policy.
     */
    public static VehiclePolicy refVehiclePolicy() {
        return new NearestVehiclePolicy();
    }

    /**
     * The reference request allocation policy for calculating the reference objective.
     *
     * @return the reference policy.
     */
    public static RequestPolicy refRequestPolicy() {
        return new NearestRequestPolicy();
    }
}
