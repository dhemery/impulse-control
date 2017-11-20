package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.SettableBooleanValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BooleanTogglerMode implements BiConsumer<Integer, Integer> {
    private final String name;
    private final List<BooleanToggler> actions = new ArrayList<>();

    public BooleanTogglerMode(String name, List<SettableBooleanValue> targets, Function<Integer,Boolean> shouldToggle) {
        this(name);
        targets.stream().map(p -> new BooleanToggler(p, shouldToggle)).forEach(actions::add);
    }

    public BooleanTogglerMode(String name) {
        this.name = name;
    }

    @Override
    public void accept(Integer index, Integer value) {
        actions.get(index).accept(value);
    }

    public void enter() {
        actions.forEach(BooleanToggler::activate);
    }

    public void exit() {
        actions.forEach(BooleanToggler::deactivate);
    }

    @Override
    public String toString() {
        return name;
    }
}
