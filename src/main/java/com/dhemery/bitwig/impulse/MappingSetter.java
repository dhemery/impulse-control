package com.dhemery.bitwig.impulse;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class MappingSetter<SV, T, TV> implements Consumer<SV> {
    private final T target;
    private final Function<SV, TV> mapper;
    private final BiConsumer<T, ? super TV> setter;

    public MappingSetter(T target, Function<SV, TV> mapper, BiConsumer<T, ? super TV> setter) {
        this.target = target;
        this.mapper = mapper;
        this.setter = setter;
    }

    protected T target() {
        return target;
    }

    @Override
    public void accept(SV sourceValue) {
        setter.accept(target, mapper.apply(sourceValue));
    }
}
