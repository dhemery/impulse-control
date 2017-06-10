package com.dhemery.impulse;

public class ToggleButton extends Control {
    private final int valueWhenOn;
    private final int valueWhenOff;

    public ToggleButton(String name, int channel, int cc, int valueWhenOn, int valueWhenOff) {
        super(name, channel, cc);
        this.valueWhenOn = valueWhenOn;
        this.valueWhenOff = valueWhenOff;
    }

    public int valueWhenOn() {
        return valueWhenOn;
    }

    public int valueWhenOff() {
        return valueWhenOff;
    }
}
