package com.dhemery.impulse.controls;

import com.dhemery.impulse.ControlIdentifier;

/**
 * Represents a control on a Novation Impulse.
 */
public class Control {
    public final ControlIdentifier identifier;

    public Control(ControlIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return String.format("%s %s", getClass().getSimpleName(), identifier);
    }
}
