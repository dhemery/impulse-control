package com.dhemery.bitwig.commands;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.NoteInput;

import java.util.function.IntConsumer;

public class ForwardToNoteInput implements IntConsumer {
    private final NoteInput noteInput;
    private final int cc;

    public ForwardToNoteInput(NoteInput noteInput, int cc) {
        this.noteInput = noteInput;
        this.cc = cc;
    }

    @Override
    public void accept(int value) {
        noteInput.sendRawMidiEvent(ShortMidiMessage.CONTROL_CHANGE, cc, value);
    }
}
