package com.dhemery.bitwig.impulse;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.dhemery.midi.Control;
import com.dhemery.midi.ControlChangeDispatcher;
import com.dhemery.midi.ControlChangeMessage;
import com.dhemery.midi.ControlIdentifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Delivers each control change message to the action registered to the control.
 */
public class ControlChangeProcessor implements ShortMidiMessageReceivedCallback, ControlChangeDispatcher {
    private final Map<ControlIdentifier, Consumer<ControlChangeMessage>> actionsByControl = new HashMap<>();
    private final Consumer<ControlChangeMessage> unknownMidiMessageAction;

    public ControlChangeProcessor(Consumer<ControlChangeMessage> unknownMidiMessageAction) {
        this.unknownMidiMessageAction = unknownMidiMessageAction;
    }

    @Override
    public void onMessage(Control control, Consumer<ControlChangeMessage> action) {
        actionsByControl.merge(control.identifier(), action, Consumer::andThen);
    }

    @Override
    public void onValue(Control control, IntConsumer action) {
        onMessage(control, m -> action.accept(m.value()));
    }

    @Override
    public <T extends Control> void onValue(T control, BiConsumer<? super T, Integer> action) {
        onMessage(control, m -> action.accept(control, m.value()));
    }

    @Override
    public void onTouch(Control control, Runnable action) {
        onMessage(control, m -> action.run());
    }

    @Override
    public void midiReceived(ShortMidiMessage message) {
        ControlChangeMessage ccMessage = new ControlChangeMessage(message.getChannel(), message.getData1(), message.getData2());
        actionsByControl.getOrDefault(ccMessage.identifier(), unknownMidiMessageAction).accept(ccMessage);
    }
}
