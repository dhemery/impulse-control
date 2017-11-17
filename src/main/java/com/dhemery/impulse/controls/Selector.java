package com.dhemery.impulse.controls;

import com.bitwig.extension.controller.api.MidiOut;
import com.dhemery.impulse.ControlIdentifier;

public class Selector extends Control {
    public Selector(ControlIdentifier identifier) {
        super(identifier);
    }

    public void select(MidiOut out) {
        set(out, 1);
    }

    private void set(MidiOut out, int value) {
        out.sendMidi(0xB0 + identifier.channel, identifier.cc, value);
    }
}
