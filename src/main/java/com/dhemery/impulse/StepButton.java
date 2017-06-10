package com.dhemery.impulse;

import com.dhemery.midi.ControlIdentifier;

public class StepButton extends Control {
    private final int minimumValue;
    private final int maximumValue;
    private final int stepSize;

    public StepButton(String name, ControlIdentifier identifier, int minimumValue, int maximumValue, int stepSize) {
        super(name, identifier);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.stepSize = stepSize;
    }

    public int stepSize() {
        return stepSize;
    }

    public int minimumValue() {
        return minimumValue;
    }

    public int maximumValue() {
        return maximumValue;
    }
}
