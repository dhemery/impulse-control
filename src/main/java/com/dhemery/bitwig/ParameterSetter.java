package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.impulse.MappingSetter;
import com.dhemery.bitwig.impulse.Service;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ParameterSetter extends MappingSetter<Integer, Parameter, Double> implements Service {
    public ParameterSetter(Parameter target, Function<Integer, Double> mapper, BiConsumer<? super Parameter, ? super Double> setter) {
        super(target, mapper, setter);
    }

    @Override
    public void activate() {
        target().setIndication(true);
    }

    @Override
    public void deactivate() {
        target().setIndication(false);
    }
}
