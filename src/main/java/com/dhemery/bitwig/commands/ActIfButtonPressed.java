package com.dhemery.bitwig.commands;

import java.util.function.IntConsumer;

public class ActIfButtonPressed implements IntConsumer {
    private final Runnable action;

    public ActIfButtonPressed(Runnable action) {
        this.action = action;
    }

    @Override
    public void accept(int value) {
        if (value > 0) action.run();
    }
}