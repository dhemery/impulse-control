package com.dhemery.bitwig.impulse;

import java.util.function.Consumer;
import java.util.function.Function;

public class MappingModeSetter extends MappingSetter<Integer, Consumer<Mode>, Mode> {
    public MappingModeSetter(Consumer<Mode> target, Function<Integer, Mode> mapper) {
        super(target, mapper, Consumer::accept);
    }
}
