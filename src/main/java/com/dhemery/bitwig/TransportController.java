package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.Transport;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.MomentaryButton;
import com.dhemery.midi.ControlChangeDispatcher;

/**
 * Coordinates interaction between the Impulse transport buttons and the Bitwig transport.
 */
public class TransportController {
    private final Transport transport;
    private final ControlChangeDispatcher dispatcher;

    public TransportController(Transport transport, Impulse impulse, ControlChangeDispatcher dispatcher) {
        this.transport = transport;
        this.dispatcher = dispatcher;
        SettableBooleanValue loopEnabled = transport.isArrangerLoopEnabled();
        loopEnabled.markInterested();
        onPress(impulse.playButton(), transport::play);
        onPress(impulse.stopButton(), transport::stop);
        onPress(impulse.rewindButton(), transport::rewind);
        onPress(impulse.fastForwardButton(), transport::fastForward);
        onPress(impulse.loopButton(), loopEnabled::toggle);
        onPress(impulse.recordButton(), transport::record);
    }

    private void onPress(MomentaryButton button, Runnable action) {
        dispatcher.register(button, button.ifPressed(action));
    }
}

