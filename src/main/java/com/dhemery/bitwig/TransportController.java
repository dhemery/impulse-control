package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.Transport;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.function.IntConsumer;

/**
 * Coordinates interaction between the Impulse transport buttons and the Bitwig transport.
 */
public class TransportController {
    public TransportController(Transport transport, Impulse impulse, ControlChangeDispatcher dispatcher) {
        SettableBooleanValue loopEnabled = transport.isArrangerLoopEnabled();
        loopEnabled.markInterested();
        dispatcher.register(impulse.playButton(), onPress(transport::play));
        dispatcher.register(impulse.stopButton(), onPress(transport::stop));
        dispatcher.register(impulse.rewindButton(), onPress(transport::rewind));
        dispatcher.register(impulse.fastForwardButton(), onPress(transport::fastForward));
        dispatcher.register(impulse.loopButton(), onPress(loopEnabled::toggle));
        dispatcher.register(impulse.recordButton(), onPress(transport::record));
    }

    private IntConsumer onPress(Runnable action) {
        return v -> {
            if (v > 0) action.run();
        };
    }
}

