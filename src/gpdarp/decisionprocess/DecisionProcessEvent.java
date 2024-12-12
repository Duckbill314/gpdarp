package gpdarp.decisionprocess;

/**
 * An abstract decision process event.
 * It has a time that the event occurs.
 * Natural comparison prefers the earlier event.
 *
 * @author gphhucarp
 */
public abstract class DecisionProcessEvent implements Comparable<DecisionProcessEvent> {
    protected int time;

    public DecisionProcessEvent(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public abstract void trigger(DecisionProcess decisionProcess);

    @Override
    public int compareTo(DecisionProcessEvent o) { return Integer.compare(time, o.time); }
}
