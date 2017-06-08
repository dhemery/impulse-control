package com.dhemery.bitwig.commands;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.dhemery.bitwig.ControlChangeCommand;

import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

/**
 * Acts on a CC message sent by an encoder that sends INCREMENT (0x41) and DECREMENT (0x3f) values.
 */
public class StepperCommand extends ControlChangeCommand {
    private static final int STEPPER_BASE_VALUE = 0x40;
    private static final IntUnaryOperator NORMALIZE_STEP = v -> v - STEPPER_BASE_VALUE;

    public StepperCommand(IntConsumer action) {
        super(NORMALIZE_STEP, action);
    }
}
