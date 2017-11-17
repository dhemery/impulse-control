package com.dhemery.bitwig.impulse;

import java.util.function.IntConsumer;

public class RunOnButtonPress implements IntConsumer {
    private final Runnable action;

    public RunOnButtonPress(Runnable action) {
        this.action = action;
    }

    @Override
    public void accept(int value) {
        if (value > 0) action.run();
    }
}