package gpdarp.core;

/**
 * The only circumstance in which a position is fixed and reused is if it is the position of a recharging station.
 * This class emphasises the importance of these positions, giving a point of reference for charging tasks.
 *
 * @author William Huang
 */
public class Station extends Node {
    private final int id;

    public Station(int id, int x, int y) {
        super(x, y);
        setType(NodeType.STATION);
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public Station clone() { return new Station(id, getX(), getY()); }
}
