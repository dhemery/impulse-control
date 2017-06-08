package com.dhemery.bitwig.commands;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.dhemery.bitwig.Command;

import java.util.function.IntConsumer;

/**
 * Acts on a CC message sent by a control with a range of 0..127.
 */
public class SelectorCommand implements Command {
    private final IntConsumer action;

    public SelectorCommand(IntConsumer action) {
        this.action = action;
    }

    @Override
    public void accept(ShortMidiMessage message) {
        action.accept(message.getData2());
    }
}
