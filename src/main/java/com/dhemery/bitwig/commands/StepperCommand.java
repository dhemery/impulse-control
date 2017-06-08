package com.dhemery.bitwig.commands;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.dhemery.bitwig.Command;

import java.util.function.IntConsumer;

/**
 * Acts on a CC message sent by an encoder that sends INCREMENT (0x41) and DECREMENT (0x3f) values.
 */
public class StepperCommand implements Command {
    private static final int STEPPER_BASE_VALUE = 0x40;
    private final IntConsumer action;

    public StepperCommand(IntConsumer action) {
        this.action = action;
    }

    @Override
    public void accept(ShortMidiMessage message) {
        action.accept(message.getData2() - STEPPER_BASE_VALUE);
    }
}
