package com.dhemery.bitwig.impulse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Mode<T extends Consumer<Integer>> implements BiConsumer<Integer, Integer> {
    private final String name;
    private final List<T> actors = new ArrayList<>();

    public Mode(String name) {
        this(name, Collections.emptyList());
    }

    public Mode(String name, List<T> actors) {
        this.name = name;
        this.actors.addAll(actors);
    }

    @Override
    public void accept(Integer index, Integer value) {
        actors.get(index).accept(value);
    }

    @Override
    public String toString() {
        return name;
    }

    public void enter() {}
    public void exit() {}

    protected void eachActor(Consumer<? super T> action) {
        actors.forEach(action);
    }
}
