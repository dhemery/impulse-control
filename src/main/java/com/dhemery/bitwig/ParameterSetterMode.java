package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.impulse.ServiceMode;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParameterSetterMode extends ServiceMode {
    public ParameterSetterMode(String name, List<Parameter> parameters, Function<Integer, Double> mapper, BiConsumer<? super Parameter, Double> setter) {
        super(name, parameters.stream().map(p -> new ParameterSetter(p, mapper, setter)).collect(Collectors.toList()));
    }
}
