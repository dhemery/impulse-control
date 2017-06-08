package com.dhemery.bitwig;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.NoteInput;
import com.dhemery.midi.ControlChangeDispatcher;
import com.dhemery.midi.ControlIdentifier;

import java.util.function.IntConsumer;

public class NoteInputController {
    private static final String[] NOTE_INPUT_MESSAGE_MASKS = {
            // Impulse does not send key aftertouch (A?????) messages, so no need to handle them.
            "8?????",   // Note Off, any channel.
            "9?????",   // Note On, any channel.
            "B201??",   // Mod Wheel (CC 1), channel 2.
            // The extension handles all other CC messages.
            "C?????",   // Program Change (any channel.
            "D?????",   // Channel Aftertouch, any channel.
            "E?????"    // Pitch Bend, any channel.
    };

    // Encoder CCs when encoders are in MIDI mode
    private static final int ENCODER_CHANNEL = 0;
    private static final int ENCODER_BASE_CONTROL = 0x15;
    private static final int ENCODER_COUNT = 8;

    // Fader CCs when faders are in MIDI mode
    private static final int FADER_CHANNEL = 0;
    private static final int FADER_BASE_CONTROL = 0x29;
    private static final int FADER_COUNT = 9;

    // Mute/Solo button CCs when faders are in MIDI mode
    private static final int BUTTON_CHANNEL = 0;
    private static final int BUTTON_BASE_CONTROL = 0x33;
    private static final int BUTTON_COUNT = 9;
    private final ControlChangeDispatcher dispatcher;
    private final Display display;
    private final NoteInput noteInput;

    public NoteInputController(MidiIn port, String name, ControlChangeDispatcher dispatcher, Display display) {
        this.dispatcher = dispatcher;
        this.display = display;
        noteInput = port.createNoteInput(name, NOTE_INPUT_MESSAGE_MASKS);
        registerControls(BUTTON_COUNT, BUTTON_BASE_CONTROL, BUTTON_CHANNEL);
        registerControls(ENCODER_COUNT, ENCODER_BASE_CONTROL, ENCODER_CHANNEL);
        registerControls(FADER_COUNT, FADER_BASE_CONTROL, FADER_CHANNEL);
    }

    private void registerControls(int count, int baseCC, int channel) {
        for(int i = 0 ; i < count ; i++) registerControl(i, baseCC, channel);
    }

    private void registerControl(int index, int baseCC, int channel) {
        int cc = baseCC + index;
        ControlIdentifier identifier = new ControlIdentifier(channel, cc);
        dispatcher.register(identifier, newValue -> noteInput.sendRawMidiEvent(ShortMidiMessage.CONTROL_CHANGE, cc, newValue));
    }
}
