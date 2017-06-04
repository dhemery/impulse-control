package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.Transport;
import com.dhemery.midi.ControlChangeMessenger;

/**
 * Coordinates interaction between the Impulse transport buttons and the Bitwig transport.
 */
public class TransportController {
    private static final int TRANSPORT_BUTTON_CC_CHANNEL = 0;
    private static final int FAST_FORWARD_BUTTON = 0x1C;
    private static final int PLAY_BUTTON = 0x1E;
    private static final int REWIND_BUTTON = 0x1B;
    private static final int STOP_BUTTON = 0x1D;
    private static final int PRESSED = 1;
    private final Transport transport;

    public TransportController(Transport transport, ControlChangeMessenger messenger) {
        this.transport = transport;
        messenger.register(TRANSPORT_BUTTON_CC_CHANNEL, PLAY_BUTTON, this::play);
        messenger.register(TRANSPORT_BUTTON_CC_CHANNEL, STOP_BUTTON, this::stop);
        messenger.register(TRANSPORT_BUTTON_CC_CHANNEL, REWIND_BUTTON, this::rewind);
        messenger.register(TRANSPORT_BUTTON_CC_CHANNEL, FAST_FORWARD_BUTTON, this::fastForward);
    }

    private void play(int state) {
        if (state == PRESSED) transport.play();
    }

    private void stop(int state) {
        if (state == PRESSED) transport.stop();
    }

    private void fastForward(int state) {
        if (state == PRESSED) transport.fastForward();
    }

    private void rewind(int state) {
        if (state == PRESSED) transport.rewind();
    }
}

