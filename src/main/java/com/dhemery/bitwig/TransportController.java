package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.Transport;
import com.dhemery.midi.Control;
import com.dhemery.midi.ControlChangeMessenger;

import javax.sound.midi.ShortMessage;

/**
 * Coordinates interaction between the Impulse transport buttons and the Bitwig transport.
 */
public class TransportController {
    private static final int TRANSPORT_BUTTON_CC_CHANNEL = 0;
    private static final int FAST_FORWARD_BUTTON = 0x1C;
    private static final int PLAY_BUTTON = 0x1E;
    private static final int REWIND_BUTTON = 0x1B;
    private static final int STOP_BUTTON = 0x1D;
    private final Transport transport;

    public TransportController(Transport transport, ControlChangeMessenger messenger) {
        this.transport = transport;
        messenger.register(new Control(TRANSPORT_BUTTON_CC_CHANNEL, PLAY_BUTTON), this::play);
        messenger.register(new Control(TRANSPORT_BUTTON_CC_CHANNEL, STOP_BUTTON), this::stop);
        messenger.register(new Control(TRANSPORT_BUTTON_CC_CHANNEL, REWIND_BUTTON), this::rewind);
        messenger.register(new Control(TRANSPORT_BUTTON_CC_CHANNEL, FAST_FORWARD_BUTTON), this::fastForward);
    }

    private void play(ShortMessage message) {
        if (message.getData2() > 0) transport.play();
    }

    private void stop(ShortMessage message) {
        if (message.getData2() > 0) transport.stop();
    }

    private void fastForward(ShortMessage message) {
        if (message.getData2() > 0) transport.fastForward();
    }

    private void rewind(ShortMessage message) {
        if (message.getData2() > 0) transport.rewind();
    }
}

