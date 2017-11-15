package com.dhemery.midi;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.dhemery.impulse.Control;
import com.dhemery.impulse.ControlIdentifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Delivers each control change message to the action registered to the control.
 */
public class ControlChangeProcessor implements ShortMidiMessageReceivedCallback, ControlChangeDispatcher {
    private final Map<ControlIdentifier, IntConsumer> actionsByControl = new HashMap<>();
    private final Consumer<ShortMidiMessage> unknownMidiMessageAction;

    public ControlChangeProcessor(Consumer<ShortMidiMessage> unknownMidiMessageAction) {
        this.unknownMidiMessageAction = unknownMidiMessageAction;
    }

    @Override
    public void register(Control control, IntConsumer action) {
        actionsByControl.put(control.identifier, action);
    }

    @Override
    public void midiReceived(ShortMidiMessage message) {
        ControlIdentifier controlIdentifier = controlIdentifierFor(message);
        if (actionsByControl.containsKey(controlIdentifier)) {
            actionsByControl.get(controlIdentifier).accept(message.getData2());
        } else {
            unknownMidiMessageAction.accept(message);
        }
    }

    private static ControlIdentifier controlIdentifierFor(ShortMidiMessage message) {
        return new ControlIdentifier(message.getChannel(), message.getData1());
    }
}