package cc.zenith.features.modules.api.settings.impl;

import cc.zenith.features.modules.api.settings.Value;

public class BooleanValue extends Value<Boolean> {

    public BooleanValue(String name, boolean defaultValue) {
        super(name, defaultValue, null);
    }
}
