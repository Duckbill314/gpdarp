package gphhucarp.core;

public class Node {
    private int id;
    private double x;
    private double y;

    public Node(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() { return id; }

    public double getX() { return x; }

    public double getY() { return y; }

    public String toString() {
        return String.format("(%f, %f)", x, y);
    }

    public Double calcDist(Node o) {
        return Math.sqrt(Math.pow((o.getX() - x), 2) + Math.pow((o.getY() - y), 2));
    }
}
