package com.dhemery.impulse;

import com.dhemery.midi.Control;
import com.dhemery.midi.ControlIdentifier;

public class Encoder extends Control implements Normalizing {
    private static final int ENCODER_VALUE_OFFSET = 0x40;

    public Encoder(ControlIdentifier identifier) {
        super(identifier);
    }

    @Override
    public double normalize(int value) {
        return value - ENCODER_VALUE_OFFSET;
    }
}
