package cc.slack.features.modules.api.settings.impl;

import cc.slack.features.modules.api.settings.Value;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MultiSelectValue extends Value<Object> {

    private final HashMap<String, Boolean> booleans = new LinkedHashMap<>();

    public MultiSelectValue(String name) {
        super(name, true, null);
    }

    public MultiSelectValue addOption (String name, boolean defaultValue) {
        booleans.put(name.toLowerCase(), defaultValue);
        return this;
    }

    @Override
    public void setValue(Object value) {
    }

    @Override
    public Object getValue() {
        return null;
    }

    public boolean getValue(String name) {
        return booleans.get(name.toLowerCase());
    }

    public HashMap<String, Boolean> getValues() {
        return booleans;
    }

    public void setValue(String name, boolean value) {
        if (booleans.containsKey(name.toLowerCase())) {
            booleans.put(name, value);
        }
    }
}
