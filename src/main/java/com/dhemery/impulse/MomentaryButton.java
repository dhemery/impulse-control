package com.dhemery.impulse;

import java.util.function.IntConsumer;

/**
 * A button that sends one value when presses, another when released.
 */
public class MomentaryButton extends Control {
    /**
     * Creates a momentary button.
     *
     * @param identifier identifies the button
     */
    public MomentaryButton(ControlIdentifier identifier) {
        super(identifier);
    }

    public IntConsumer ifPressed(Runnable action) {
        return ccValue -> { if(ccValue > 0) { action.run(); } };
    }
}
