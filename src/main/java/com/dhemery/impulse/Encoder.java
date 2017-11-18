package com.dhemery.impulse;

import com.dhemery.midi.Control;
import com.dhemery.midi.ControlIdentifier;

public class Encoder extends Control {
    private static final int BASE_VALUE = 0x40;

    public Encoder(ControlIdentifier identifier) {
        super(identifier);
    }

    public int size(int value) {
        return value - BASE_VALUE;
    }
}
