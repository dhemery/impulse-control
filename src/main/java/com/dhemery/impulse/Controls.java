package com.dhemery.impulse;

import com.dhemery.bitwig.Studio;

import javax.sound.midi.ShortMessage;
import java.util.function.Consumer;

/**
 * The set of controls on an Impulse 49/61.
 */
public class Controls implements Consumer<ShortMessage>  {
    private final Studio studio;

    public Controls(Studio studio) {
        this.studio = studio;
    }

    @Override
    public void accept(ShortMessage message) {
        studio.debug("Received MIDI %X%X[%02X,%02X]", message.getCommand() >> 4, message.getChannel(), message.getData1(), message.getData2());
    }
}
