package com.dhemery.impulse.controls;

public interface Normalizing {
    /**
     * Normalizes the given CC value to the range 0..1 (for unipolar controls)
     * or -1..1 (for bipolar controls).
     * @param value the value to normalize
     * @return the normalized value
     */
    double normalize(int value);
}
