package com.dhemery.bitwig;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.NoteInput;
import com.dhemery.midi.ControlChangeDispatcher;
import com.dhemery.midi.ControlIdentifier;

import java.util.function.IntConsumer;

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

    // Encoder CCs when encoders are in MIDI mode
    // TODO: Resolve conflict between encoder 7 and Rewind (both send CC 0x1B)
    // TODO: Resolve conflict between encoder 8 and Rewind (both send CC 0x1C)
    private static final int ENCODER_CHANNEL = 0;
    private static final int ENCODER_BASE_CC = 0x15;
    private static final int ENCODER_COUNT = 8;

    // Fader CCs when faders are in MIDI mode
    // TODO: Resolve conflict between fader 1 and Impulse's Program Change controls (all send CC 0x29)
    private static final int FADER_CHANNEL = 0;
    private static final int FADER_BASE_CC = 0x29;
    private static final int FADER_COUNT = 9;

    // Mute/Solo button CCs when faders are in MIDI mode
    private static final int BUTTON_CHANNEL = 0;
    private static final int BUTTON_BASE_CC = 0x33;
    private static final int BUTTON_COUNT = 9;

    private static final int MOD_WHEEL_CHANNEL = 2;
    private static final int MOD_WHEEL_CC = 1;

    private final ControlChangeDispatcher dispatcher;
    private final Display display;
    private final NoteInput noteInput;

    public NoteInputController(MidiIn port, String name, ControlChangeDispatcher dispatcher, Display display) {
        this.dispatcher = dispatcher;
        this.display = display;
        noteInput = port.createNoteInput(name, NOTE_INPUT_MESSAGE_MASKS);
        registerControls(BUTTON_CHANNEL, BUTTON_BASE_CC, BUTTON_COUNT);
        registerControls(ENCODER_CHANNEL, ENCODER_BASE_CC, ENCODER_COUNT);
        registerControls(FADER_CHANNEL, FADER_BASE_CC, FADER_COUNT);
    }

    private void registerControls(int channel, int baseCC, int count) {
        for (int i = 0; i < count; i++) registerControl(channel, baseCC + i);
    }

    private void registerControl(int channel, int cc) {
        dispatcher.register(new ControlIdentifier(channel, cc), forwardToNoteInput(cc));
    }

    private IntConsumer forwardToNoteInput(int cc) {
        return newValue -> noteInput.sendRawMidiEvent(ShortMidiMessage.CONTROL_CHANGE, cc, newValue);
    }
}
