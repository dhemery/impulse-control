package com.dhemery.bitwig.commands;

import com.dhemery.bitwig.ControlChangeCommand;

import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

/**
 * Acts on a CC message sent by a control with a range of 0..127.
 */
public class SelectorCommand extends ControlChangeCommand {
    public SelectorCommand(IntConsumer action) {
        super(IntUnaryOperator.identity(), action);
    }
}
