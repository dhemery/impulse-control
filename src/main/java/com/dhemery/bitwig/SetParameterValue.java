package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.Parameter;

public class SetParameterValue extends AdjustParameter {
    private static final int CC_VALUE_RANGE = 128;

    public SetParameterValue(Parameter parameter) {
        super(parameter);
    }

    @Override
    public void accept(int value) {
        parameter.set(value, CC_VALUE_RANGE);
    }
}
