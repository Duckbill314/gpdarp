package gpdarp.core;

import java.util.ArrayList;
import java.util.List;

/**
 * The only circumstance in which a position is fixed and reused is if it is the position of a recharging station.
 * This class emphasises the importance of these positions, giving a point of reference for charging tasks.
 *
 * @author William Huang
 */
public class Station extends Node {
    public Station(int x, int y) {
        super(x, y);
        setType(NodeType.STATION);
    }

    @Override
    public String toString() { return String.format("Station at %s", super.toString()); }

    /**
     * Utility method for creating deep clones of ArrayLists of Stations.
     *
     * @param stations the list of stations to be cloned.
     *
     * @return the cloned list.
     */
    public static List<Station> listClone(List<Station> stations) {
        List<Station> clonedStations = new ArrayList<>();
        for (Station station : stations) {
            clonedStations.add((Station) station.clone());
        }
        return clonedStations;
    }

    @Override
    public Station clone() {
        return (Station) super.clone();
    }
}
