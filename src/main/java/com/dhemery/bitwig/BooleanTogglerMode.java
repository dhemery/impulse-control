package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.dhemery.bitwig.impulse.ServiceMode;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BooleanTogglerMode extends ServiceMode {
    public BooleanTogglerMode(String name, List<SettableBooleanValue> targets, Function<Integer, Boolean> shouldToggle) {
        super(name, targets.stream().map(p -> new BooleanToggler(p, shouldToggle)).collect(Collectors.toList()));
    }
}
