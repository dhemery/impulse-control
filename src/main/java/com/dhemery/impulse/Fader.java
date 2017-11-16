package com.dhemery.impulse;

public class Fader extends Control implements Normalizing {
    private static final int FADER_CONTROL_MAX_VALUE = 127;

    public Fader(ControlIdentifier identifier) {
        super(identifier);
    }

    @Override
    public double normalize(int value) {
        return ((double) value) / FADER_CONTROL_MAX_VALUE;
    }
}
