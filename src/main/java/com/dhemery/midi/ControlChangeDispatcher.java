package com.dhemery.midi;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Delivers each control change message to the action associated with the control.
 */
public class ControlChangeDispatcher implements ShortMidiMessageReceivedCallback {
    private final Map<ControlIdentifier, Consumer<ShortMidiMessage>> actionsByControl = new HashMap<>();
    private final Consumer<ShortMidiMessage> unknownMidiMessageAction;

    public ControlChangeDispatcher(Consumer<ShortMidiMessage> unknownMidiMessageAction) {
        this.unknownMidiMessageAction = unknownMidiMessageAction;
    }

    public void register(ControlIdentifier identifier, Consumer<ShortMidiMessage> action) {
        actionsByControl.put(identifier, action);
    }

    @Override
    public void midiReceived(ShortMidiMessage message) {
        actionsByControl
                .getOrDefault(sourceOf(message), unknownMidiMessageAction)
                .accept(message);
    }

    private static ControlIdentifier sourceOf(ShortMidiMessage message) {
        return new ControlIdentifier(message.getChannel(), message.getData1());
    }
}
