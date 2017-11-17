package com.dhemery.impulse;

import com.dhemery.midi.Control;
import com.dhemery.midi.ControlIdentifier;

public class Fader extends Control implements Normalizing {
    private static final int FADER_MAX_VALUE = 127;

    public Fader(ControlIdentifier identifier) {
        super(identifier);
    }

    @Override
    public double normalize(int value) {
        return ((double) value) / FADER_MAX_VALUE;
    }
}
