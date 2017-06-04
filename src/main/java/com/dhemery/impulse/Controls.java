package com.dhemery.impulse;

import com.dhemery.bitwig.Display;

import javax.sound.midi.ShortMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The set of controls on an Impulse 49/61.
 */
public class Controls implements Consumer<ShortMessage> {
    private final Map<Integer, Consumer<ShortMessage>> controlsByIdentifier = new HashMap<>();
    private final Consumer<ShortMessage> unknownControlHandler;

    public Controls(Consumer<ShortMessage> unknownControlHandler) {
        this.unknownControlHandler = unknownControlHandler;
    }

    public void install(Control control) {
        controlsByIdentifier.put(control.identifier(), control);
    }

    @Override
    public void accept(ShortMessage message) {
        controlsByIdentifier
                .getOrDefault(controlIdentifierFor(message), unknownControlHandler)
                .accept(message);
    }

    private static int controlIdentifierFor(ShortMessage message) {
        return (message.getCommand() + message.getChannel()) << 8 + message.getData1();
    }
}
