package com.dhemery.bitwig.impulse;

import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.MomentaryButton;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class ButtonBankController implements Consumer<Mode> {
    private final Bitwig bitwig;
    private Mode currentMode;
    private final String name;

    public ButtonBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher, Mode initialMode) {
        name = "Buttons";
        this.bitwig = bitwig;
        currentMode = initialMode;

        List<? extends MomentaryButton> buttons = impulse.mixerButtons();
        IntStream.range(0, buttons.size())
                .forEach(i -> dispatcher.onValue(buttons.get(i), v -> currentMode.accept(i, v)));
    }

    public void debug(String message) {
        bitwig.debug(format("%s: %s", this, message));
    }

    @Override
    public void accept(Mode newMode) {
        if (currentMode == newMode) return;
        currentMode.exit();
        currentMode = newMode;
        currentMode.enter();
        bitwig.debug(format("%s %s", this, format("-> %s", currentMode)));
    }

    @Override
    public String toString() {
        return name;
    }
}
