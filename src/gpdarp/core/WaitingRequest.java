package gpdarp.core;

public class WaitingRequest extends Request {
    public boolean isRequest = false;

    public WaitingRequest(int id, int tRec, Node pickup, Node dropoff, int tEarly, int tLate, int tMax, int demand) {
        super(id, tRec, pickup, dropoff, tEarly, tLate, tMax, demand);
    }

    public WaitingRequest(Request request) {
        super(request.getId(), request.getTRec(), request.getPickup(), request.getDropoff(), request.getTEarly(),
                request.getTLate(), request.getTMax(), request.getDemand());
        isRequest = true;
    }

    public WaitingRequest(int time, Node pos, Node station) {
        super(-1, time, pos, station, 0, (int) Double.POSITIVE_INFINITY, (int) Double.POSITIVE_INFINITY, 0);
    }

    @Override
    public WaitingRequest clone() {
        return (WaitingRequest) super.clone();
    }
}
