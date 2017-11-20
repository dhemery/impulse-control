package com.dhemery.bitwig.impulse;

import com.dhemery.impulse.Encoder;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class EncoderController implements Consumer<Mode> {
    private final String name;
    private final Consumer<String> observer;
    private Mode currentMode;

    public EncoderController(Impulse impulse, ControlChangeDispatcher dispatcher, Mode initialMode, Consumer<String> observer) {
        this.observer = observer;
        name = "Encoders";
        currentMode = initialMode;

        List<Encoder> encoders = impulse.mixerEncoders();
        IntStream.range(0, encoders.size())
                .forEach(i -> dispatcher.onValue(encoders.get(i), v -> currentMode.accept(i, v)));
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
