package com.dhemery.midi;

/**
 * Identifies a controlNumber message by its channel and controlNumber number.
 */
public class ControlIdentifier {
    public final int channel;
    public final int cc;

    public ControlIdentifier(int channel, int cc) {
        this.channel = channel;
        this.cc = cc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ControlIdentifier that = (ControlIdentifier) o;

        if (channel != that.channel) return false;
        return cc == that.cc;
    }

    @Override
    public int hashCode() {
        return 31 * channel + cc;
    }
}
