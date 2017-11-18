package com.dhemery.midi;

/**
 * Represents a control on a Novation Impulse.
 */
public class Control {
    private final ControlIdentifier identifier;

    public Control(ControlIdentifier identifier) {
        this.identifier = identifier;
    }

    public ControlIdentifier identifier() {
        return identifier;
    }

    public int channel() {
        return identifier().channel();
    }

    public int cc() {
        return identifier().cc();
    }

    public int status() {
        return identifier().status();
    }

    @Override
    public String toString() {
        return String.format("%s %s", getClass().getSimpleName(), identifier);
    }
}
