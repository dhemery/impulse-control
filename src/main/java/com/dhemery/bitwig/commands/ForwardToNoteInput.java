package com.dhemery.bitwig.commands;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.NoteInput;

import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

public class ForwardToNoteInput implements BiConsumer<Integer,Integer> {
    private final NoteInput noteInput;

    public ForwardToNoteInput(NoteInput noteInput) {
        this.noteInput = noteInput;
    }

    @Override
    public void accept(Integer cc, Integer value) {
        noteInput.sendRawMidiEvent(ShortMidiMessage.CONTROL_CHANGE, cc, value);
    }
}
