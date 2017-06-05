package com.dhemery.bitwig;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.Transport;
import com.dhemery.midi.Control;
import com.dhemery.midi.MidiMessenger;

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

    public TransportController(Transport transport, MidiMessenger messenger) {
        this.transport = transport;
        messenger.register(new Control(TRANSPORT_BUTTON_CC_CHANNEL, PLAY_BUTTON), this::play);
        messenger.register(new Control(TRANSPORT_BUTTON_CC_CHANNEL, STOP_BUTTON), this::stop);
        messenger.register(new Control(TRANSPORT_BUTTON_CC_CHANNEL, REWIND_BUTTON), this::rewind);
        messenger.register(new Control(TRANSPORT_BUTTON_CC_CHANNEL, FAST_FORWARD_BUTTON), this::fastForward);
    }

    private void play(ShortMidiMessage message) {
        if (message.getData2() > 0) transport.play();
    }

    private void stop(ShortMidiMessage message) {
        if (message.getData2() > 0) transport.stop();
    }

    private void fastForward(ShortMidiMessage message) {
        if (message.getData2() > 0) transport.fastForward();
    }

    private void rewind(ShortMidiMessage message) {
        if (message.getData2() > 0) transport.rewind();
    }
}

