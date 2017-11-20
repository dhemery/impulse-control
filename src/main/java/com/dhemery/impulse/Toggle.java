package com.dhemery.impulse;

import com.dhemery.midi.Control;
import com.dhemery.midi.ControlIdentifier;

public class Toggle extends Control {
    public Toggle(ControlIdentifier identifier) {
        super(identifier);
    }

    public static boolean isOn(int value) {
        return value > 0;
    }
}
