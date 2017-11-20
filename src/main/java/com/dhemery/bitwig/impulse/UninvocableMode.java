package com.dhemery.bitwig.impulse;

import java.util.Collections;
import java.util.function.Consumer;

public class UninvocableMode extends Mode {
    private final Consumer<String> observer;

    public UninvocableMode(String name, Consumer<String> observer) {
        super(name, Collections.emptyList());
        this.observer = observer;
    }

    @Override
    public void accept(Integer index, Integer value) {
        observer.accept(String.format("%s mode invoked with index %d and value %x", this, index, value));
    }
}
