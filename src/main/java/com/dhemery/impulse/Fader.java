package com.dhemery.impulse;

import com.dhemery.midi.Control;
import com.dhemery.midi.ControlIdentifier;

public class Fader extends Control {
    public static final int MAX_VALUE = 127;

    public Fader(ControlIdentifier identifier) {
        super(identifier);
    }
}
