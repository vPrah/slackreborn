package cc.zenith.events.impl.input;

import cc.zenith.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeyEvent extends Event {
    private final int key;
}
