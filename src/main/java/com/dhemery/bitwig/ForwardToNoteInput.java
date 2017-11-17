package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.NoteInput;
import com.dhemery.midi.ControlChangeMessage;

import java.util.function.Consumer;

public class ForwardToNoteInput implements Consumer<ControlChangeMessage> {
    private final NoteInput noteInput;

    public ForwardToNoteInput(NoteInput noteInput) {
        this.noteInput = noteInput;
    }

    @Override
    public void accept(ControlChangeMessage message) {
        noteInput.sendRawMidiEvent(message.status(), message.cc(), message.value());
    }
}
