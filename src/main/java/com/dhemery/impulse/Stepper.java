package com.dhemery.impulse;

public class Stepper extends Control implements Normalizing {
    private static final int STEPPER_CONTROL_OFFSET = 0x40;

    public Stepper(ControlIdentifier identifier) {
        super(identifier);
    }

    @Override
    public double normalize(int value) {
        return value - STEPPER_CONTROL_OFFSET;
    }
}
