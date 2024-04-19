package cc.zenith.events;

import cc.zenith.Zenith;

public class Event {
    private boolean cancel;

    public Event call() {
        Zenith.getInstance().getEventBus().publish(this);
        return this;
    }

    public boolean isCanceled() {
        return cancel;
    }

    public void cancel() {
        this.cancel = true;
    }
}
