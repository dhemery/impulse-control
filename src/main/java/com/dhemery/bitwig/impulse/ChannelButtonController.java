package com.dhemery.bitwig.impulse;

import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.function.Consumer;

public class ChannelButtonController extends ControlChangeController {
    public ChannelButtonController(Impulse impulse, ControlChangeDispatcher dispatcher, Mode initialMode, Consumer<String> observer) {
        super(impulse.mixerButtons(), dispatcher, initialMode, observer);
    }

}
