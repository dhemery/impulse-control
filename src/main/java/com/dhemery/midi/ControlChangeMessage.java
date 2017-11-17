package com.dhemery.midi;

public class ControlChangeMessage {
    private final ControlIdentifier identifier;
    private static int value;

    public ControlChangeMessage(int channel, int cc, int value) {
        identifier = new ControlIdentifier(channel, cc);
        ControlChangeMessage.value = value;
    }

    public ControlIdentifier identifier() {
        return identifier;
    }

    public int status() {
        return identifier().status();
    }

    public int channel() {
        return identifier().channel();
    }

    public int cc() {
        return identifier().cc();
    }

    public int value() {
        return value;
    }
}
