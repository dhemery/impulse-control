package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.Transport;
import com.dhemery.midi.ControlChangeDispatcher;
import com.dhemery.midi.ControlIdentifier;

import java.util.function.IntConsumer;

/**
 * Coordinates interaction between the Impulse transport buttons and the Bitwig transport.
 */
public class TransportController {
    private static final int TRANSPORT_BUTTON_CC_CHANNEL = 0;
    private static final int FAST_FORWARD_BUTTON = 0x1C;
    private static final int PLAY_BUTTON = 0x1E;
    private static final int REWIND_BUTTON = 0x1B;
    private static final int STOP_BUTTON = 0x1D;
    private static final int LOOP_BUTTON = 0x1F;
    private static final int RECORD_BUTTON = 0x20;

    public TransportController(Transport transport, ControlChangeDispatcher messenger) {
        SettableBooleanValue loopEnabled = transport.isArrangerLoopEnabled();
        loopEnabled.markInterested();
        messenger.register(new ControlIdentifier(TRANSPORT_BUTTON_CC_CHANNEL, PLAY_BUTTON), onPress(transport::play));
        messenger.register(new ControlIdentifier(TRANSPORT_BUTTON_CC_CHANNEL, STOP_BUTTON), onPress(transport::stop));
        messenger.register(new ControlIdentifier(TRANSPORT_BUTTON_CC_CHANNEL, REWIND_BUTTON), onPress(transport::rewind));
        messenger.register(new ControlIdentifier(TRANSPORT_BUTTON_CC_CHANNEL, FAST_FORWARD_BUTTON), onPress(transport::fastForward));
        messenger.register(new ControlIdentifier(TRANSPORT_BUTTON_CC_CHANNEL, LOOP_BUTTON), onPress(loopEnabled::toggle));
        messenger.register(new ControlIdentifier(TRANSPORT_BUTTON_CC_CHANNEL, RECORD_BUTTON), onPress(transport::record));
    }

    private IntConsumer onPress(Runnable action) {
        return v -> {
            if (v > 0) action.run();
        };
    }
}

