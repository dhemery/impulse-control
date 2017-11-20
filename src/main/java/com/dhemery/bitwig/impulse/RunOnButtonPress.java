package com.dhemery.bitwig.impulse;

import java.util.function.Consumer;

public class RunOnButtonPress implements Consumer<Integer> {
    private final Runnable action;

    public RunOnButtonPress(Runnable action) {
        this.action = action;
    }

    @Override
    public void accept(Integer value) {
        if (value > 0) action.run();
    }
}