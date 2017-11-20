package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.dhemery.bitwig.impulse.MappingSetter;
import com.dhemery.bitwig.impulse.Service;

import java.util.function.Function;

public class BooleanToggler extends MappingSetter<Integer, SettableBooleanValue, Boolean> implements Service {
    public BooleanToggler(SettableBooleanValue target, Function<Integer,Boolean> shouldToggle) {
        super(target, shouldToggle, (t, b) -> { if(b) t.toggle();});
    }

    @Override
    public void activate() {
        target().subscribe();
    }

    @Override
    public void deactivate() {
        target().unsubscribe();
    }
}
