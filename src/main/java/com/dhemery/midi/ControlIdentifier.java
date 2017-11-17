package com.dhemery.midi;

/**
 * Identifies a control by its channel and cc number.
 */
public class ControlIdentifier {
    private final int channel;
    private final int cc;

    public ControlIdentifier(int channel, int cc) {
        this.channel = channel;
        this.cc = cc;
    }

    public int channel() {
        return channel;
    }

    public int cc() {
        return cc;
    }

    public int status() {
        return 0xB0 + channel;
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

    @Override
    public String toString() {
        return String.format("channel %2x cc %2x", channel, cc);
    }
}
