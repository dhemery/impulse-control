package com.dhemery.impulse;

/**
 * Represents a control on a Novation Impulse.
 */
public class Control {
    public final ControlIdentifier identifier;
    public final ControlRange range;

    /**
     * Creates a control.
     * @param identifier identifies the control
     * @param range describes the set of CC values the control can send
     */
    public Control(ControlIdentifier identifier, ControlRange range) {
        this.identifier = identifier;
        this.range = range;
    }
}
