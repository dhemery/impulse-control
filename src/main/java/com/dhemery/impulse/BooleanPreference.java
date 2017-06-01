package com.dhemery.impulse;

import com.bitwig.extension.callback.EnumValueChangedCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableEnumValue;

import java.util.function.Consumer;

public class BooleanPreference implements EnumValueChangedCallback {
    private final String[] STATES = new String[]{"OFF", "ON"};
    private final Consumer<Boolean> client;

    public BooleanPreference(ControllerHost host, String category, String name, Consumer<Boolean> client) {
        this.client = client;
        SettableEnumValue preference = host.getPreferences().getEnumSetting(name, category, STATES, STATES[0]);
        preference.addValueObserver(this);
    }

    @Override
    public void valueChanged(String newValue) {
        client.accept(STATES[1].equals(newValue));
    }
}
