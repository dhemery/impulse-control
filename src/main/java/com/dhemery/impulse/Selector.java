package com.dhemery.impulse;

import com.bitwig.extension.controller.api.MidiOut;
import com.dhemery.midi.Control;
import com.dhemery.midi.ControlIdentifier;

public class Selector extends Control {
    public Selector(ControlIdentifier identifier) {
        super(identifier);
    }

    public void select(MidiOut out) {
        set(out, 1);
    }

    private void set(MidiOut out, int value) {
        out.sendMidi(0xB0 + channel(), cc(), value);
    }
}
