package gphhucarp.core;

public class Request {
    private int id;
    private float tRec;
    private Node pickup;
    private Node dropoff;
    private float tEarly;
    private float tLate;
    private float tMax;

    public Request(int id, float tRec, Node pickup, Node dropoff, float tEarly, float tLate, float tMax) {
        this.id = id;
        this.tRec = tRec;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.tEarly = tEarly;
        this.tLate = tLate;
        this.tMax = tMax;
    }

    public int getId() { return id; }

    public float getTRec() { return tRec; }

    public Node getPickup() { return pickup; }

    public Node getDropoff() { return dropoff; }

    public float getTEarly() { return tEarly; }

    public float getTLate() { return tLate; }

    public float getTMax() { return tMax; }
}
