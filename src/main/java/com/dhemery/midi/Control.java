package com.dhemery.midi;

import javax.sound.midi.ShortMessage;

public class Control {
    private final int command;
    private final int control;

    public Control(int channel, int control) {
        this.command = channel;
        this.control = control;
    }

    // Assumes, without verifying, that the message is a CC message.
    public static Control forSenderOf(ShortMessage message) {
        return new Control(message.getChannel(), message.getData1());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Control that = (Control) o;

        if (command != that.command) return false;
        return control == that.control;
    }

    @Override
    public int hashCode() {
        return 31 * command + control;
    }
}
