package com.dhemery.bitwig.commands;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.NoteInput;
import com.dhemery.impulse.controls.Control;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ForwardToNoteInput implements Consumer<ShortMidiMessage> {
    private final NoteInput noteInput;

    public ForwardToNoteInput(NoteInput noteInput) {
        this.noteInput = noteInput;
    }

    @Override
    public void accept(ShortMidiMessage message) {
        noteInput.sendRawMidiEvent(message.getStatusByte(), message.getData1(), message.getData2());
    }
}
