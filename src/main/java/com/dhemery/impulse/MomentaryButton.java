package com.dhemery.impulse;

import com.dhemery.midi.Control;
import com.dhemery.midi.ControlIdentifier;

public class MomentaryButton extends Control {
    public MomentaryButton(ControlIdentifier identifier) {
        super(identifier);
    }

    public void ifPressed(int value, Runnable action) {
        if(value > 0) action.run();
    }
}
