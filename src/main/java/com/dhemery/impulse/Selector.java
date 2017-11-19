package com.dhemery.impulse;

import com.dhemery.midi.Control;
import com.dhemery.midi.ControlIdentifier;

import java.util.function.ObjIntConsumer;

public class Selector extends Control {
    public static final int SELECT_COMMAND = 1;

    public Selector(ControlIdentifier identifier) {
        super(identifier);
    }

    public void select(ObjIntConsumer<? super Selector> action) {
        action.accept(this, SELECT_COMMAND);
    }
}
