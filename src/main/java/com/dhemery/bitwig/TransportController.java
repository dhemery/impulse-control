package com.dhemery.bitwig;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.Transport;
import com.dhemery.midi.ControlIdentifier;
import com.dhemery.midi.ControlChangeDispatcher;

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
    private final Transport transport;
    private final SettableBooleanValue loopEnabled;

    public TransportController(Transport transport, ControlChangeDispatcher messenger) {
        this.transport = transport;
        loopEnabled = transport.isArrangerLoopEnabled();
        loopEnabled.addValueObserver(b -> {}); // Bitwig requires an observer, but we don't care about changes.
        messenger.register(new ControlIdentifier(TRANSPORT_BUTTON_CC_CHANNEL, PLAY_BUTTON), this::play);
        messenger.register(new ControlIdentifier(TRANSPORT_BUTTON_CC_CHANNEL, STOP_BUTTON), this::stop);
        messenger.register(new ControlIdentifier(TRANSPORT_BUTTON_CC_CHANNEL, REWIND_BUTTON), this::rewind);
        messenger.register(new ControlIdentifier(TRANSPORT_BUTTON_CC_CHANNEL, FAST_FORWARD_BUTTON), this::fastForward);
        messenger.register(new ControlIdentifier(TRANSPORT_BUTTON_CC_CHANNEL, LOOP_BUTTON), this::loop);
        messenger.register(new ControlIdentifier(TRANSPORT_BUTTON_CC_CHANNEL, RECORD_BUTTON), this::record);
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

    private void loop(ShortMidiMessage message) {
        if (message.getData2() > 0) loopEnabled.toggle();
    }

    private void record(ShortMidiMessage message) {
        if (message.getData2() > 0) transport.record();
    }
}

