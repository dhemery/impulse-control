package com.dhemery.impulse;

import com.bitwig.extension.controller.api.MidiOut;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Impulse {
    private static final String SYSEX_MESSAGE_START = "F0";
    private static final String NOVATION_ID = "00 20 29";
    private static final String SENDER_ID = "67";
    private static final String SYSEX_MESSAGE_END = "F7";
    private static final String MESSAGE_FORMAT = String.join(" ",
            SYSEX_MESSAGE_START,
            NOVATION_ID,
            SENDER_ID,
            "%s",
            SYSEX_MESSAGE_END);
    private static final String CONNECT_TO_COMPUTER = sysexMessage("06 01 01 01");
    private static final int BUTTON_COUNT = 9;
    private static final int ENCODER_COUNT = 8;
    private static final int FADER_COUNT = 9;
    private static final int MIDI_BUTTON_CHANNEL = 0;
    private static final int MIDI_BUTTON_BASE_CC = 0x33;
    private static final int MIDI_ENCODER_CHANNEL = 1;
    private static final int MIDI_ENCODER_BASE_CC = 0x15;
    private static final int MIDI_FADER_CHANNEL = 0;
    private static final int MIDI_FADER_BASE_CC = 0x29;
    private static final int MIXER_BUTTON_CHANNEL = 0;
    private static final int MIXER_BUTTON_BASE_CC = 0x09;
    private static final int MIXER_ENCODER_CHANNEL = 1;
    private static final int MIXER_ENCODER_BASE_CC = 0x00;
    private static final int MIXER_FADER_CHANNEL = 0;
    private static final int MIXER_FADER_BASE_CC = 0x00;
    private static final ControlRange FULL_CC_RANGE = new ControlRange(0, 128, 1);
    private static final ControlRange MIDI_BUTTON_RANGE = new ControlRange(0, 2,0x7f);
    private static final ControlRange TOGGLE_BUTTON_RANGE = new ControlRange(0, 2, 1);
    private static final ControlRange STEP_ENCODER_RANGE = new ControlRange(0x3f,2, 2);
    private final List<Control> midiButtons = makeControls(MIDI_BUTTON_CHANNEL, MIDI_BUTTON_BASE_CC, MIDI_BUTTON_RANGE, BUTTON_COUNT);
    private final List<Control> midiEncoders = makeControls(MIDI_ENCODER_CHANNEL, MIDI_ENCODER_BASE_CC, FULL_CC_RANGE, ENCODER_COUNT);
    private final List<Control> midiFaders = makeControls(MIDI_FADER_CHANNEL, MIDI_FADER_BASE_CC, FULL_CC_RANGE, FADER_COUNT);
    private final List<Control> mixerButtons = makeControls(MIXER_BUTTON_CHANNEL, MIXER_BUTTON_BASE_CC, TOGGLE_BUTTON_RANGE, BUTTON_COUNT);
    private final List<Control> mixerEncoders = makeControls(MIXER_ENCODER_CHANNEL, MIXER_ENCODER_BASE_CC, STEP_ENCODER_RANGE, ENCODER_COUNT);
    private final List<Control> mixerFaders = makeControls(MIXER_FADER_CHANNEL, MIXER_FADER_BASE_CC, FULL_CC_RANGE, FADER_COUNT);

    public Impulse(MidiOut port) {
        port.sendSysex(CONNECT_TO_COMPUTER);
    }

    private static List<Control> makeControls(int channel, int ccBase, ControlRange range, int count) {
        return IntStream.range(0, count)
                .mapToObj(index -> new ControlIdentifier(channel,ccBase+index))
                .map(identifier -> new Control(identifier, range))
                .collect(Collectors.toList());
    }

    public List<Control> midiButtons() {
        return midiButtons;
    }

    public List<Control> midiEncoders() {
        return midiEncoders;
    }

    public List<Control> midiFaders() {
        return midiFaders;
    }

    public List<Control> mixerButtons() {
        return mixerButtons;
    }

    public List<Control> mixerEncoders() {
        return mixerEncoders;
    }

    public List<Control> mixerFaders() {
        return mixerFaders;
    }

    private static String sysexMessage(String content) {
        return String.format(MESSAGE_FORMAT, content);
    }
}
