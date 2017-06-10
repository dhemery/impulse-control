package com.dhemery.impulse;

import com.dhemery.midi.ControlIdentifier;

public class Control {
    private final String name;
    private final ControlIdentifier identifier;

    protected Control(String name, int channel, int cc) {
        this(name, new ControlIdentifier(channel, cc));
    }

    protected Control(String name, ControlIdentifier identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    public ControlIdentifier identifier() {
        return identifier;
    }
}
