package com.dhemery.midi;

/**
 * Identifies a controlNumber message by its channel and controlNumber number.
 */
public class ControlIdentifier {
    private final int channel;
    private final int controlNumber;

    public ControlIdentifier(int channel, int controlNumber) {
        this.channel = channel;
        this.controlNumber = controlNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ControlIdentifier that = (ControlIdentifier) o;

        if (channel != that.channel) return false;
        return controlNumber == that.controlNumber;
    }

    @Override
    public int hashCode() {
        return 31 * channel + controlNumber;
    }
}
