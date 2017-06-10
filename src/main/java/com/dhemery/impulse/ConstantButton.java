package com.dhemery.impulse;

import com.dhemery.midi.ControlIdentifier;

public class ConstantButton extends Control {
    private final int value;

    public ConstantButton(String name, ControlIdentifier identifier, int value) {
        super(name, identifier);
        this.value = value;
    }

    public int value() {
        return value;
    }
}
