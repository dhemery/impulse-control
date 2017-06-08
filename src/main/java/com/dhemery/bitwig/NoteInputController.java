package com.dhemery.bitwig;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.MidiIn;
import com.dhemery.midi.ControlIdentifier;
import com.dhemery.midi.ControlChangeDispatcher;

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
    private static final int MIDI_ENCODER_CHANNEL = 1;
    private static final int MIDI_ENCODER_BASE_CONTROL = 0x15;
    private static final int MIDI_ENCODER_COUNT = 8;

    // Fader CCs when faders are in MIDI mode
    private static final int MIDI_FADER_CHANNEL = 0;
    private static final int MIDI_FADER_BASE_CONTROL = 0x29;
    private static final int MIDI_FADER_COUNT = 9;

    // Mute/Solo button CCs when faders are in MIDI mode
    private static final int MIDI_MUTE_SOLO_BUTTON_CHANNEL = 0;
    private static final int MIDI_MUTE_SOLO_BUTTON_BASE_CONTROL = 0x33;
    private static final int MIDI_MUTE_SOLO_BUTTON_COUNT = 9;
    private final Display display;

    public NoteInputController(MidiIn port, String name, ControlChangeDispatcher dispatcher, Display display) {
        this.display = display;
        port.createNoteInput(name, NOTE_INPUT_MESSAGE_MASKS);
        for (int i = MIDI_ENCODER_BASE_CONTROL; i < MIDI_ENCODER_BASE_CONTROL + MIDI_ENCODER_COUNT; i++)
            dispatcher.register(new ControlIdentifier(MIDI_ENCODER_CHANNEL, i), this::onEncoderChange);
    }

    private void onEncoderChange(ShortMidiMessage message) {
        display.debug(String.format("New value %2x for MIDI encoder %2x", message.getData2(), message.getData1()));
    }
}
