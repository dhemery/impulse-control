package com.dhemery.bitwig.impulse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ServiceMode<T extends Consumer<Integer> & Service> implements BiConsumer<Integer, Integer> {
    private final String name;
    private final List<T> actions = new ArrayList<>();

    public ServiceMode(String name, List<T> targets) {
        this(name);
        actions.addAll(targets);
    }

    public ServiceMode(String name) {
        this.name = name;
    }

    @Override
    public void accept(Integer index, Integer value) {
        actions.get(index).accept(value);
    }

    public void enter() {
        actions.forEach(Service::activate);
    }

    public void exit() {
        actions.forEach(Service::deactivate);
    }

    @Override
    public String toString() {
        return name;
    }
}
