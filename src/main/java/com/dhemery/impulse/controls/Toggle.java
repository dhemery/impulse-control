package com.dhemery.impulse.controls;

import com.bitwig.extension.controller.api.MidiOut;
import com.dhemery.impulse.ControlIdentifier;

public class Toggle extends Control {
    public Toggle(ControlIdentifier identifier) {
        super(identifier);
    }

    public void enable(MidiOut out) {
        set(out, 1);
    }

    public void disable(MidiOut out) {
        set(out, 0);
    }

    private void set(MidiOut out, int value) {
        out.sendMidi(0xB0 + identifier.channel, identifier.cc, value);
    }
}
