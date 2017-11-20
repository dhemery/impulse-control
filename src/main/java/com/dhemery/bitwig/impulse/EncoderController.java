package com.dhemery.bitwig.impulse;

import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.function.Consumer;

public class EncoderController extends ControlChangeController {
    public EncoderController(Impulse impulse, ControlChangeDispatcher dispatcher, Mode initialMode, Consumer<String> observer) {
        super(impulse.mixerEncoders(), dispatcher, initialMode, observer);
    }
}
