package com.dhemery.bitwig.impulse;

import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.MomentaryButton;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class ChannelButtonController implements Consumer<Mode> {
    private final Consumer<String> observer;
    private Mode currentMode;
    private final String name;

    public ChannelButtonController(Impulse impulse, ControlChangeDispatcher dispatcher, Mode initialMode, Consumer<String> observer) {
        this.observer = observer;
        name = "Buttons";
        currentMode = initialMode;

        List<? extends MomentaryButton> buttons = impulse.mixerButtons();
        IntStream.range(0, buttons.size())
                .forEach(i -> dispatcher.onValue(buttons.get(i), v -> currentMode.accept(i, v)));
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
