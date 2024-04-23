package cc.slack.utils.other;

public final class MSTimer {

    public long time = -1L;

    public boolean hasTimePassed(final long MS) {
        return System.currentTimeMillis() >= time + MS;
    }

    public long hasTimeLeft(final long MS) {
        return (MS + time) - System.currentTimeMillis();
    }

    public long getTimeElapsed() { return System.currentTimeMillis() - time; }

    public void reset() {
        time = System.currentTimeMillis();
    }
}
