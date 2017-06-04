package com.dhemery.impulse;

import javax.sound.midi.ShortMessage;
import java.util.function.Consumer;

/**
 * Represents a single control on the Novation Impulse.
 */
public class Control implements Consumer<ShortMessage> {
    private final int identifier;
    private final Consumer<ShortMessage> action;

    public Control(int statusCode, int controllerNumber, Consumer<ShortMessage> action) {
        identifier = statusCode << 8 + controllerNumber;
        this.action = action;
    }

    public final int identifier() {
        return identifier;
    }

    public void accept(ShortMessage message) {
        action.accept(message);
    }
}
