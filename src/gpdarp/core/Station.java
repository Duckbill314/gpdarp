package gpdarp.core;

/**
 * The only circumstance in which a position is fixed and reused is if it is the position of a recharging station.
 * This class emphasises the importance of these positions, giving a point of reference for charging tasks.
 *
 * @author William Huang
 */
public class Station extends Node {
    public Station(double x, double y) {
        super(x, y);
    }

    @Override
    public String toString() { return String.format("Station at %s", super.toString()); }
}
