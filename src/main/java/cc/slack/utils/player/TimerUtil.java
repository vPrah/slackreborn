package cc.slack.utils.player;

import cc.slack.utils.client.IMinecraft;

public class TimerUtil implements IMinecraft {

    private static final float DEFAULT_TIMER = 1.0F;

    public static void set(float timer) {
        setTimer(timer, 0);
    }

    public static void setTick(float timer, int tick) {
        setTimer(timer, tick);
    }

    public static void setTickOnGround(float timer, int tick) {
        setTimer(mc.thePlayer.onGround ? timer : DEFAULT_TIMER, tick);
    }

    public static void setOnGround(float timer) {
        setTimer(mc.thePlayer.onGround ? timer : DEFAULT_TIMER, 0);
    }

    private static void setTimer(float timer, int tick) {
        if (!shouldTimer()) {
            reset();
            return;
        }

        if (tick == 0)
            mc.timer.timerSpeed = timer;
        else
            mc.timer.timerSpeed = mc.thePlayer.ticksExisted % tick == 0 ? timer : DEFAULT_TIMER;
    }

    private static boolean shouldTimer() {
        return (mc.thePlayer != null && mc.theWorld != null) && mc.thePlayer.isEntityAlive();
    }

    public static void reset() {
        mc.timer.timerSpeed = DEFAULT_TIMER;
    }
}
