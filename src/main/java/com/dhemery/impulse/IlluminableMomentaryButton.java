package com.dhemery.impulse;

import com.dhemery.midi.ControlIdentifier;

import java.util.function.ObjIntConsumer;

public class IlluminableMomentaryButton extends MomentaryButton {
    private static final int DIM_COMMAND = 0;
    private static final int ILLUMINATE_COMMAND = 1;

    public IlluminableMomentaryButton(ControlIdentifier identifier) {
        super(identifier);
    }

    public void set(boolean illuminationState, ObjIntConsumer<? super IlluminableMomentaryButton> action) {
        action.accept(this, illuminationState ? ILLUMINATE_COMMAND : DIM_COMMAND);
    }
}
