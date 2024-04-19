package cc.zenith.utils.player;

import cc.zenith.utils.client.mc;

public class TimerUtil extends mc {

    private static final float DEFAULT_TIMER = 1.0F;

    public static void set(float timer) {
        setTimer(timer, 0);
    }

    public static void setTick(float timer, int tick) {
        setTimer(timer, tick);
    }

    public static void setTickOnGround(float timer, int tick) {
        setTimer(getPlayer().onGround ? timer : DEFAULT_TIMER, tick);
    }

    public static void setOnGround(float timer) {
        setTimer(getPlayer().onGround ? timer : DEFAULT_TIMER, 0);
    }

    private static void setTimer(float timer, int tick) {
        if (!shouldTimer()) {
            reset();
            return;
        }

        if (tick == 0)
            getTimer().timerSpeed = timer;
        else
            getTimer().timerSpeed = mc.getPlayer().ticksExisted % tick == 0 ? timer : DEFAULT_TIMER;
    }

    private static boolean shouldTimer() {
        return (getPlayer() != null && getWorld() != null) && getPlayer().isEntityAlive();
    }

    public static void reset() {
        getTimer().timerSpeed = DEFAULT_TIMER;
    }
}
