package com.dhemery.bitwig.impulse;

import java.util.function.Consumer;

public class SingletonModeSetter implements Runnable {
    private final Consumer<Mode> actor;
    private final Mode mode;

    public SingletonModeSetter(Consumer<Mode> actor, Mode mode) {
        this.actor = actor;
        this.mode = mode;
    }
    @Override
    public void run() {
        actor.accept(mode);
    }
}
