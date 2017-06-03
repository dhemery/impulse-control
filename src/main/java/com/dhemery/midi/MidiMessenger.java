package com.dhemery.midi;

import com.dhemery.bitwig.impulse.ImpulseControlException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import java.util.function.Consumer;

/**
 * Delivers each three-byte MIDI message to the destination as a {@link ShortMessage}.
 */
public class MidiMessenger {
    private Consumer<ShortMessage> destination;

    public MidiMessenger(Consumer<ShortMessage> destination) {
        this.destination = destination;
    }

    public void deliver(int status, int data1, int data2) {
        destination.accept(shortMessage(status, data1, data2));
    }

    private ShortMessage shortMessage(int status, int data1, int data2) {
        try {
            return new ShortMessage(status, data1, data2);
        } catch (InvalidMidiDataException cause) {
            throw new ImpulseControlException("Cannot create MIDI short message", cause);
        }
    }
}
