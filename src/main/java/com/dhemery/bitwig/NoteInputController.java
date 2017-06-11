package com.dhemery.bitwig;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.NoteInput;
import com.dhemery.impulse.Control;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

public class NoteInputController {
    private static final String[] NOTE_INPUT_MESSAGE_MASKS = {
            "8?????",   // Note Off, any channel.
            "9?????",   // Note On, any channel.
            // A?????      Impulse does not send Key Aftertouch
            "B201??",   // Mod Wheel. The extension handles all other CC messages.
            "C?????",   // Program Change (any channel.
            "D?????",   // Channel Aftertouch, any channel.
            "E?????"    // Pitch Bend, any channel.
    };
    private final ControlChangeDispatcher dispatcher;
    private final Display display;
    private final NoteInput noteInput;

    public NoteInputController(MidiIn port, String name, Impulse impulse, ControlChangeDispatcher dispatcher, Display display) {
        this.dispatcher = dispatcher;
        this.display = display;
        noteInput = port.createNoteInput(name, NOTE_INPUT_MESSAGE_MASKS);
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
