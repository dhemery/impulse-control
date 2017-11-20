package com.dhemery.bitwig.impulse;

import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.function.Consumer;

public class ChannelFaderController extends ControlChangeController {
    public ChannelFaderController(Impulse impulse, ControlChangeDispatcher dispatcher, Mode initialMode, Consumer<String> observer) {
        super(impulse.mixerFaders(), dispatcher, initialMode, observer);
    }
}
