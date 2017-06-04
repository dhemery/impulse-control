package com.dhemery.midi;

import javax.sound.midi.ShortMessage;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Executes an action upon receipt of a MIDI Control Change message.
 */
public class ControlChangeAction implements Consumer<ShortMessage> {
    private final int identifier;
    private final IntConsumer action;

    public ControlChangeAction(int channel, int controller, IntConsumer action) {
        this.action = action;
        identifier = identifierFor(channel, controller);
    }

    public final int identifier() {
        return identifier;
    }

    @Override
    public void accept(ShortMessage message) {
        action.accept(message.getData2());
    }

    public static int identifierFor(ShortMessage message) {
        return identifierFor(message.getCommand(), message.getChannel(), message.getData1());
    }

    private static int identifierFor(int channel, int controller) {
        return identifierFor(ShortMessage.CONTROL_CHANGE, channel, controller);
    }

    private static int identifierFor(int command, int channel, int controller) {
        return (command + channel) << 8 + controller;
    }
}
