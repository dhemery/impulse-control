package com.dhemery.impulse;

/**
 * Represents a control on a Novation Impulse.
 */
public class Control {
    public final ControlIdentifier identifier;

    public  Control(ControlIdentifier identifier) {
        this.identifier = identifier;
    }
}
