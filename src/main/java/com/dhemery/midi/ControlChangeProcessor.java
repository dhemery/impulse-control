package com.dhemery.midi;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.dhemery.bitwig.commands.ActIfButtonPressed;
import com.dhemery.impulse.ControlIdentifier;
import com.dhemery.impulse.controls.Control;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Delivers each control change message to the action registered to the control.
 */
public class ControlChangeProcessor implements ShortMidiMessageReceivedCallback, ControlChangeDispatcher {
    private final Map<ControlIdentifier, Consumer<ShortMidiMessage>> actionsByControl = new HashMap<>();
    private final Consumer<ShortMidiMessage> unknownMidiMessageAction;

    public ControlChangeProcessor(Consumer<ShortMidiMessage> unknownMidiMessageAction) {
        this.unknownMidiMessageAction = unknownMidiMessageAction;
    }

    @Override
    public void onMessage(Control control, Consumer<ShortMidiMessage> action) {
        actionsByControl.put(control.identifier, action);
    }

    @Override
    public void onValue(Control control, IntConsumer action) {
        onMessage(control, m -> action.accept(m.getData2()));
    }

    @Override
    public <T extends Control> void onValue(T control, BiConsumer<? super T, Integer> action) {
        onMessage(control, m -> action.accept(control, m.getData2()));
    }

    @Override
    public void onTouch(Control control, Runnable action) {
        onMessage(control, m -> action.run());
    }

    @Override
    public void midiReceived(ShortMidiMessage message) {
        ControlIdentifier controlIdentifier = controlIdentifierFor(message);
        actionsByControl.getOrDefault(controlIdentifier, unknownMidiMessageAction).accept(message);
    }

    private static ControlIdentifier controlIdentifierFor(ShortMidiMessage message) {
        return new ControlIdentifier(message.getChannel(), message.getData1());
    }
}
