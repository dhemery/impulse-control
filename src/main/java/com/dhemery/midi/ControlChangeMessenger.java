package com.dhemery.midi;

import javax.sound.midi.ShortMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Delivers each control change message to the action associated with the control.
 */
public class ControlChangeMessenger implements Consumer<ShortMessage> {
    private final Map<Integer, Consumer<ShortMessage>> controlsByIdentifier = new HashMap<>();
    private final Consumer<ShortMessage> unknownMidiMessageAction;

    public ControlChangeMessenger(Consumer<ShortMessage> unknownMidiMessageAction) {
        this.unknownMidiMessageAction = unknownMidiMessageAction;
    }

    public void register(ControlChangeAction action) {
        controlsByIdentifier.put(action.identifier(), action);
    }

    public void register(int channel, int controller, IntConsumer action) {
        register(new ControlChangeAction(channel, controller, action));
    }

    @Override
    public void accept(ShortMessage message) {
        controlsByIdentifier
                .getOrDefault(ControlChangeAction.identifierFor(message), unknownMidiMessageAction)
                .accept(message);
    }
}
