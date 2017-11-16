package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.Parameter;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntToDoubleFunction;

public class ParameterController implements IntConsumer {
    private final IntToDoubleFunction normalizer;
    private final Consumer<Double> action;
    private final double scale;

    public ParameterController(Parameter parameter, IntToDoubleFunction normalizer, Consumer<Double> action, double scale) {
        this.normalizer = normalizer;
        this.action = action;
        this.scale = scale;
        parameter.name().markInterested();
        parameter.markInterested();
        parameter.setIndication(true);
    }

    @Override
    public void accept(int value) {
        action.accept(scale * normalizer.applyAsDouble(value));
    }
}
