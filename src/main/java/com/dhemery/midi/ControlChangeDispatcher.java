package com.dhemery.midi;

import com.dhemery.impulse.controls.Control;

import java.util.function.IntConsumer;

public interface ControlChangeDispatcher {
    void register(Control control, IntConsumer action);
}
