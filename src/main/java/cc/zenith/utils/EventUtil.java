package cc.zenith.utils;

import cc.zenith.Zenith;
import cc.zenith.events.Event;

public final class EventUtil {
    public static void register(Object o) {
        Zenith.getInstance().getEventBus().subscribe(o);
    }

    public static void unRegister(Object o) {
        Zenith.getInstance().getEventBus().unsubscribe(o);
    }

    public static void call(Event e) {
        Zenith.getInstance().getEventBus().publish(e);
    }
}
