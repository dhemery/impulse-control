package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.Parameter;

public class IncrementParameterValue extends AdjustParameter {
    private static final int OFFSET = 0x40;
    private final int scale;

    public IncrementParameterValue(Parameter parameter, int scale) {
        super(parameter);
        this.scale = scale;
    }

    @Override
    public void accept(int value) {
        parameter.inc(value - OFFSET, scale);
    }
}
