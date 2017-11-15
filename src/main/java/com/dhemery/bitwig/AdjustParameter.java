package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.Parameter;

import java.util.function.IntConsumer;

public abstract class AdjustParameter implements IntConsumer {
    protected final Parameter parameter;

    public AdjustParameter(Parameter parameter) {
        this.parameter = parameter;
        parameter.name().markInterested();
        parameter.markInterested();
        parameter.setIndication(true);
    }
}
