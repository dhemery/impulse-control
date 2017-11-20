package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.ParameterSetter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

class ParameterSetterMode implements BiConsumer<Integer, Integer> {
    private final String name;
    private final List<ParameterSetter> actions = new ArrayList<>();

    public ParameterSetterMode(String name, List<Parameter> parameters, Function<Integer,Double> mapper, BiConsumer<? super Parameter,Double> setter) {
        this(name);
        parameters.stream().map(p -> new ParameterSetter(p, mapper, setter)).forEach(actions::add);
    }

    public ParameterSetterMode(String name) {
        this.name = name;
    }

    @Override
    public void accept(Integer index, Integer value) {
        actions.get(index).accept(value);
    }

    public void enter() {
        actions.forEach(ParameterSetter::activate);
    }

    public void exit() {
        actions.forEach(ParameterSetter::deactivate);
    }

    @Override
    public String toString() {
        return name;
    }
}
