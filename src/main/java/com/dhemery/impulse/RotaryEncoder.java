package com.dhemery.impulse;

/**
 * A control knob that sends one signal when rotated counterclockwise, another when rotated clockwise.
 */
public class RotaryEncoder extends Control {
    private static final int DIRECTION_BASE = 0x40;

    /**
     * Creates a rotary encoder.
     *
     * @param identifier identifies the encoder
     */
    public RotaryEncoder(ControlIdentifier identifier) {
        super(identifier);
    }

    /**
     * Indicates the direction of rotation represented by {@code ccValue}.
     * @param ccValue a CC value sent by a rotary encoder
     * @return {@code -1} if {@code ccValue} represents counter-clockwise rotation, {@code 1} if clockwise
     */
    public int directionOf(int ccValue) {
        return ccValue - DIRECTION_BASE;
    }
}
