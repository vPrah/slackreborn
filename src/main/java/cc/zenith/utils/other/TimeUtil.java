package cc.zenith.utils.other;

public class TimeUtil {

    private long currentMs;

    public TimeUtil() {
        reset();
    }

    public boolean reach(long milliseconds) {
        return elapsed() > milliseconds;
    }

    public void resetWithOffset(long offset) {
        this.currentMs = this.getTime() + offset;
    }

    public long elapsed() {
        return System.currentTimeMillis() - currentMs;
    }

    public void reset() {
        currentMs = System.currentTimeMillis();
    }

    private long getTime() {
        return System.nanoTime() / 1000000L;
    }

}
