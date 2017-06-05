package com.dhemery.midi;

public class Control {
    private final int command;
    private final int control;

    public Control(int channel, int control) {
        this.command = channel;
        this.control = control;
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
