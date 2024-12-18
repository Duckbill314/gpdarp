package gpdarp.core;

public class WaitingRequest extends Request {
    private RequestType type;

    public WaitingRequest(Request request) {
        super(request.getId(), request.getTRec(), request.getPickup(), request.getDropoff(), request.getTEarly(),
                request.getTLate(), request.getTMax(), request.getDemand());
        type = RequestType.REQUEST;
    }

    public WaitingRequest(int time, Node pos, Node station) {
        super(-1, time, pos, station, 0, (int) Double.POSITIVE_INFINITY, (int) Double.POSITIVE_INFINITY, 0);
        type = RequestType.CHARGE;
    }

    // Getters
    public RequestType getType() { return type; }

    // Setters
    public void setType(RequestType type) { this.type = type; }

    /**
     * Request types are responsible for handling behaviour during reactive events.
     */
    public enum RequestType {
        REQUEST,
        CHARGE
    }

    @Override
    public WaitingRequest clone() {
        WaitingRequest clone = (WaitingRequest) super.clone();
        clone.type = type;
        return clone;
    }
}
