package com.dhemery.bitwig.impulse;

import com.dhemery.impulse.Fader;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class ChannelFaderController implements Consumer<Mode> {
    private final String name;
    private final Consumer<String> observer;
    private Mode currentMode;

    public ChannelFaderController(Impulse impulse, ControlChangeDispatcher dispatcher, Mode initialMode, Consumer<String> observer) {
        this.observer = observer;
        name = "Faders";
        currentMode = initialMode;
        List<Fader> faders = impulse.mixerFaders();

        IntStream.range(0, faders.size())
                .forEach(i -> dispatcher.onValue(faders.get(i), v -> currentMode.accept(i, v)));
    }

    @Override
    public void accept(Mode mode) {
        if (currentMode == mode) return;
        currentMode.exit();
        currentMode = mode;
        currentMode.enter();
        observer.accept(format("%s %s", name, format("-> %s", currentMode)));
    }
}
