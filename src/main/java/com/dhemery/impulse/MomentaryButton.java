package com.dhemery.impulse;

import com.dhemery.midi.Control;
import com.dhemery.midi.ControlIdentifier;

public class MomentaryButton extends Control {
    public MomentaryButton(ControlIdentifier identifier) {
        super(identifier);
    }

    public boolean isPressed(int value) {
        return value > 0;
    }

    public int illuminationValue(boolean illuminationState) {
        return illuminationState ? 1 : 0;
    }
}
