package com.dhemery.impulse;

public class AbsolutePosition extends Control {
    private static final int MAX_CC_VALUE = 127;
    private static final int MIN_CC_VALUE = 0;
    private final int valueAtMinimumPosition;
    private final int valueAtMaximumPosition;

    public AbsolutePosition(String name, int channel, int cc, int valueAtMinimumPosition, int valueAtMaximumPosition) {
        super(name, channel, cc);
        this.valueAtMinimumPosition = valueAtMinimumPosition;
        this.valueAtMaximumPosition = valueAtMaximumPosition;
    }

    public AbsolutePosition(String name, int channel, int cc) {
        this(name, channel, cc, MIN_CC_VALUE, MAX_CC_VALUE);
    }

    public int valueAtMinimumPosition() {
        return valueAtMinimumPosition;
    }

    public int getValueAtMaximumPosition() {
        return valueAtMaximumPosition;
    }
}
