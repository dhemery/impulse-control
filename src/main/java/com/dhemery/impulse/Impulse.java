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
    private static final int MIDI_BUTTON_OFF_VALUE = 0;
    private static final int MIDI_BUTTON_ON_VALUE = 0x7f;
    private static final int MIDI_ENCODER_CHANNEL = 1;
    private static final int MIDI_ENCODER_BASE_CC = 0x15;
    private static final int MIDI_FADER_CHANNEL = 0;
    private static final int MIDI_FADER_BASE_CC = 0x29;
    private static final int MIXER_BUTTON_CHANNEL = 0;
    private static final int MIXER_BUTTON_BASE_CC = 0x09;
    private static final int MIXER_BUTTON_OFF_VALUE = 0;
    private static final int MIXER_BUTTON_ON_VALUE = 1;
    private static final int MIXER_ENCODER_CHANNEL = 1;
    private static final int MIXER_ENCODER_BASE_CC = 0x00;
    private static final int MIXER_ENCODER_DECREMENT_VALUE = 0x3f;
    private static final int MIXER_ENCODER_INCREMENT_VALUE = 0x41;
    private static final int MIXER_FADER_CHANNEL = 0;
    private static final int MIXER_FADER_BASE_CC = 0x00;
    private final List<ToggleButton> midiButtons;
    private final List<AbsolutePosition> midiEncoders;
    private final List<AbsolutePosition> midiFaders;
    private final List<ToggleButton> mixerButtons;
    private final List<RelativePosition> mixerEncoders;
    private final List<AbsolutePosition> mixerFaders;

    public Impulse(MidiOut port) {
        port.sendSysex(CONNECT_TO_COMPUTER);
        midiButtons = IntStream.range(0, BUTTON_COUNT)
                .mapToObj(Impulse::midiButton)
                .collect(Collectors.toList());
        midiEncoders = IntStream.range(0, ENCODER_COUNT)
                .mapToObj(Impulse::midiEncoder)
                .collect(Collectors.toList());
        midiFaders = IntStream.range(0, FADER_COUNT)
                .mapToObj(Impulse::midiFader)
                .collect(Collectors.toList());
        mixerButtons = IntStream.range(0, BUTTON_COUNT)
                .mapToObj(Impulse::mixerButton)
                .collect(Collectors.toList());
        mixerEncoders = IntStream.range(0, ENCODER_COUNT)
                .mapToObj(Impulse::mixerEncoder)
                .collect(Collectors.toList());
        mixerFaders = IntStream.range(0, FADER_COUNT)
                .mapToObj(Impulse::mixerFader)
                .collect(Collectors.toList());
    }

    public List<ToggleButton> midiButtons() {
        return midiButtons;
    }

    public List<AbsolutePosition> midiEncoders() {
        return midiEncoders;
    }

    public List<AbsolutePosition> midiFaders() {
        return midiFaders;
    }

    public List<ToggleButton> mixerButtons() {
        return mixerButtons;
    }

    public List<RelativePosition> mixerEncoders() {
        return mixerEncoders;
    }

    public List<AbsolutePosition> mixerFaders() {
        return mixerFaders;
    }

    private static ToggleButton midiButton(int index) {
        return new ToggleButton(String.format("MIDI Button %d", index+1), MIDI_BUTTON_CHANNEL, MIDI_BUTTON_BASE_CC + index, MIDI_BUTTON_ON_VALUE, MIDI_BUTTON_OFF_VALUE);
    }

    private static AbsolutePosition midiEncoder(int index) {
        return new AbsolutePosition(String.format("MIDI Encoder %d", index+1), MIDI_ENCODER_CHANNEL, MIDI_ENCODER_BASE_CC + index);
    }

    private static AbsolutePosition midiFader(int index) {
        return new AbsolutePosition(String.format("MIDI Fader %d", index+1), MIDI_FADER_CHANNEL, MIDI_FADER_BASE_CC + index);
    }

    private static ToggleButton mixerButton(int index) {
        return new ToggleButton(String.format("Mixer Button %d", index+1), MIXER_BUTTON_CHANNEL, MIXER_BUTTON_BASE_CC + index, MIXER_BUTTON_ON_VALUE, MIXER_BUTTON_OFF_VALUE);
    }

    private static RelativePosition mixerEncoder(int index) {
        return new RelativePosition(String.format("Mixer/Plugin Encodeer %d", index+1), MIXER_ENCODER_CHANNEL, MIXER_ENCODER_BASE_CC + index, MIXER_ENCODER_DECREMENT_VALUE, MIXER_ENCODER_INCREMENT_VALUE);
    }

    private static AbsolutePosition mixerFader(int index) {
        return new AbsolutePosition(String.format("Mixer Fader %d", index+1), MIXER_FADER_CHANNEL, MIXER_FADER_BASE_CC + index);
    }

    private static String sysexMessage(String content) {
        return String.format(MESSAGE_FORMAT, content);
    }
}
