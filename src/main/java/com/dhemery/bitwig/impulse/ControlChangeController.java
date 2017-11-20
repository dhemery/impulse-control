package com.dhemery.bitwig.impulse;

import com.dhemery.midi.Control;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static java.lang.String.format;

public abstract class ControlChangeController implements Consumer<Mode> {
    private final Consumer<String> observer;
    private final String name;
    private Mode currentMode;

    public ControlChangeController(List<? extends Control> controls, ControlChangeDispatcher dispatcher, Mode initialMode, Consumer<String> observer) {
        name = "Buttons";
        this.observer = observer;
        currentMode = initialMode;
        IntStream.range(0, controls.size())
                .forEach(i -> dispatcher.onValue(controls.get(i), v -> currentMode.accept(i, v)));
    }

    @Override
    public void accept(Mode newMode) {
        if (currentMode == newMode) return;
        currentMode.exit();
        currentMode = newMode;
        currentMode.enter();
        observer.accept(format("%s %s", this, format("-> %s", currentMode)));
    }

    @Override
    public String toString() {
        return name;
    }
}
