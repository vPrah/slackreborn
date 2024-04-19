package cc.zenith.features.modules.api.settings.impl;

import cc.zenith.features.modules.api.settings.Value;

import java.awt.*;

public class ColorValue extends Value<Color> {

    public ColorValue(String name, Color defaultValue) {
        super(name, defaultValue, null);
    }
}
