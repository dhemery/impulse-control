package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.controls.Encoder;
import com.dhemery.impulse.controls.Normalizing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ControlMode<T extends Normalizing> {
    private final String name;
    private final Bitwig bitwig;
    private final BiConsumer<Parameter, Double> setter;
    private final double scale;
    private final Map<Normalizing, Parameter> parametersByControl = new HashMap<>();

    public ControlMode(String name, Bitwig bitwig, List<T> controls, List<Parameter> parameters, BiConsumer<Parameter,Double> setter, double scale) {
        this.name = name;
        this.bitwig = bitwig;
        this.setter = setter;
        this.scale = scale;
        IntStream.range(0, parameters.size())
                .forEach(i -> parametersByControl.put(controls.get(i), parameters.get(i)));
    }

    public void enter() {
        setIndicators(true);
        bitwig.status(String.format("Entering %s mode", name));
    }

    public void exit() {
        setIndicators(false);
    }

    private void setIndicators(boolean isActive) {
        parametersByControl.values().forEach(p -> p.setIndication(isActive));
    }

    public void accept(T control, int value) {
        setter.accept(parametersByControl.get(control), control.normalize(value) * scale);
    }
}
