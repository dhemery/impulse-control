package com.dhemery.bitwig;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.NoteInput;
import com.dhemery.impulse.Control;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

public class NoteInputController {
    private final NoteInput noteInput;
    private final ControlChangeDispatcher dispatcher;

    public NoteInputController(NoteInput noteInput, Impulse impulse, ControlChangeDispatcher dispatcher) {
        this.noteInput = noteInput;
        this.dispatcher = dispatcher;
        impulse.midiButtons().forEach(this::forwardAllCCMessagesToNoteInput);
        impulse.midiEncoders().forEach(this::forwardAllCCMessagesToNoteInput);
        impulse.midiFaders().forEach(this::forwardAllCCMessagesToNoteInput);
    }

    private void forwardAllCCMessagesToNoteInput(Control control) {
        dispatcher.register(control, v -> sendCCMessageToNoteInput(control.identifier.cc, v));
    }

    private void sendCCMessageToNoteInput(int cc, int v) {
        noteInput.sendRawMidiEvent(ShortMidiMessage.CONTROL_CHANGE, cc, v);
    }
}
