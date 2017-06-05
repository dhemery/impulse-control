package com.dhemery.midi;

import javax.sound.midi.ShortMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Delivers each control change message to the action associated with the control.
 */
public class ControlChangeMessenger implements Consumer<ShortMessage> {
    private final Map<Control, Consumer<ShortMessage>> actionsByControl = new HashMap<>();
    private final Consumer<ShortMessage> unknownMidiMessageAction;

    public ControlChangeMessenger(Consumer<ShortMessage> unknownMidiMessageAction) {
        this.unknownMidiMessageAction = unknownMidiMessageAction;
    }

    public void register(Control control, Consumer<ShortMessage> action) {
        actionsByControl.put(control, action);
    }

    @Override
    public void accept(ShortMessage message) {
        actionsByControl
                .getOrDefault(Control.forSenderOf(message), unknownMidiMessageAction)
                .accept(message);
    }
}
