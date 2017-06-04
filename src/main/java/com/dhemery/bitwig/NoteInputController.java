package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.MidiIn;
import com.dhemery.midi.ControlChangeMessenger;

public class NoteInputController {
    // MIDI messages that match these masks are handled by the note input,
    // and are not sent to our extension's controllers.
    // TODO: Masks for faders, encoders, and mute/solo buttons when in MIDI mode
    private static final String[] NOTE_INPUT_MESSAGE_MASKS = {
            "8?????", // Note Off
            "9?????", // Note On
            "A?????", // Key Aftertouch (not sent by Impulse)
            "B201??", // Mod Wheel (CC 1 on channel 2)
            "C?????", // Program change (not sent by Impulse)
            "D?????", // Channel Aftertouch
            "E?????"  // Pitch Bend
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

    public NoteInputController(MidiIn port, String name) {
        port.createNoteInput(name, NOTE_INPUT_MESSAGE_MASKS);
    }
}
