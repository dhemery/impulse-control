package com.dhemery.impulse;

import com.dhemery.midi.ControlIdentifier;

public class MomentaryButton extends Control {
    private final int valueWhenPressed;
    private final int valueWhenReleased;

    public MomentaryButton(String name, ControlIdentifier identifier, int valueWhenReleased, int valueWhenPressed) {
        super(name, identifier);
        this.valueWhenPressed = valueWhenPressed;
        this.valueWhenReleased = valueWhenReleased;
    }

    public int valueWhenPressed() {
        return valueWhenPressed;
    }

    public int valueWhenReleased() {
        return valueWhenReleased;
    }
}
