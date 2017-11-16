package com.dhemery.bitwig.commands;

import java.util.function.BiConsumer;

public class ActIfButtonPressed implements BiConsumer<Integer, Integer> {
    private final Runnable action;

    public ActIfButtonPressed(Runnable action) {
        this.action = action;
    }

    @Override
    public void accept(Integer ignoredCC, Integer value) {
        if (value > 0) action.run();
    }
}