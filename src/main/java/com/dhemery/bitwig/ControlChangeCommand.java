package com.dhemery.bitwig;


import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

public class ControlChangeCommand implements IntConsumer {
    private final IntUnaryOperator mapper;
    private final IntConsumer action;

    public ControlChangeCommand(IntUnaryOperator mapper, IntConsumer action) {
        this.mapper = mapper;
        this.action = action;
    }

    @Override
    public void accept(int value) {
        action.accept(mapper.applyAsInt(value));
    }
}
