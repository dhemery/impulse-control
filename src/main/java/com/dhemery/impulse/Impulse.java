package com.dhemery.impulse;

import com.bitwig.extension.controller.api.MidiOut;
import com.dhemery.impulse.controls.Control;
import com.dhemery.impulse.controls.Encoder;
import com.dhemery.impulse.controls.Fader;
import com.dhemery.impulse.controls.Toggle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
    private static final int MIDI_ENCODER_CHANNEL = 0;
    private static final int MIDI_ENCODER_BASE_CC = 0x15;
    private static final int MIDI_FADER_CHANNEL = 0;
    private static final int MIDI_FADER_BASE_CC = 0x29;
    private static final int MIXER_BUTTON_CHANNEL = 0;
    private static final int MIXER_BUTTON_BASE_CC = 0x09;
    private static final int MIXER_ENCODER_CHANNEL = 1;
    private static final int MIXER_ENCODER_BASE_CC = 0x00;
    private static final int MIXER_FADER_CHANNEL = 0;

    private static final int MIXER_FADER_BASE_CC = 0x00;
    private static final int TRANSPORT_CC_CHANNEL = 0;
    private static final int FAST_FORWARD_BUTTON_CC = 0x1C;
    private static final int PLAY_BUTTON_CC = 0x1E;
    private static final int REWIND_BUTTON_CC = 0x1B;
    private static final int STOP_BUTTON_CC = 0x1D;
    private static final int LOOP_BUTTON_CC = 0x1F;
    private static final int RECORD_BUTTON_CC = 0x20;
    private static final int FADER_MODE_CC_CHANNEL = 0;
    private static final int FADER_MIXER_MODE_BUTTON_CC = 0x22;
    private static final int FADER_MIDI_MODE_BUTTON_CC = 0x21;
    private static final int ENCODER_MODE_CC_CHANNEL = 1;
    private static final int ENCODER_MIDI_MODE_BUTTON_CC = 0x08;
    private static final int ENCODER_MIXER_MODE_BUTTON_CC = 0x09;
    private static final int ENCODER_PLUGIN_MODE_BUTTON_CC = 0x0A;

    private final List<Control> midiControls = new ArrayList<>();
    private final List<Control> mixerButtons = makeControls(MIXER_BUTTON_CHANNEL, MIXER_BUTTON_BASE_CC, BUTTON_COUNT, Control::new);
    private final List<Encoder> mixerEncoders = makeControls(MIXER_ENCODER_CHANNEL, MIXER_ENCODER_BASE_CC, ENCODER_COUNT, Encoder::new);
    private final List<Fader> mixerFaders = makeControls(MIXER_FADER_CHANNEL, MIXER_FADER_BASE_CC, FADER_COUNT, Fader::new);
    private final Control playButton = makeControl(TRANSPORT_CC_CHANNEL, PLAY_BUTTON_CC, Control::new);
    private final Control stopButton = makeControl(TRANSPORT_CC_CHANNEL, STOP_BUTTON_CC, Control::new);
    private final Control rewindButton = makeControl(TRANSPORT_CC_CHANNEL, REWIND_BUTTON_CC, Control::new);
    private final Control fastForwardButton = makeControl(TRANSPORT_CC_CHANNEL, FAST_FORWARD_BUTTON_CC, Control::new);
    private final Control recordButton = makeControl(TRANSPORT_CC_CHANNEL, RECORD_BUTTON_CC, Control::new);
    private final Control loopButton = makeControl(TRANSPORT_CC_CHANNEL, LOOP_BUTTON_CC, Control::new);
    private final Toggle faderMixerModeButton = makeControl(FADER_MODE_CC_CHANNEL, FADER_MIXER_MODE_BUTTON_CC, Toggle::new);
    private final Toggle faderMidiModeButton = makeControl(FADER_MODE_CC_CHANNEL, FADER_MIDI_MODE_BUTTON_CC, Toggle::new);
    private final Toggle encoderMidiModeButton = makeControl(ENCODER_MODE_CC_CHANNEL, ENCODER_MIDI_MODE_BUTTON_CC, Toggle::new);
    private final Toggle encoderMixerModeButton = makeControl(ENCODER_MODE_CC_CHANNEL, ENCODER_MIXER_MODE_BUTTON_CC, Toggle::new);
    private final Toggle encoderPluginModeButton = makeControl(ENCODER_MODE_CC_CHANNEL, ENCODER_PLUGIN_MODE_BUTTON_CC, Toggle::new);

    public Impulse(MidiOut port) {
        port.sendSysex(CONNECT_TO_COMPUTER);
        midiControls.addAll(makeControls(MIDI_BUTTON_CHANNEL, MIDI_BUTTON_BASE_CC, BUTTON_COUNT, Control::new));
        midiControls.addAll(makeControls(MIDI_ENCODER_CHANNEL, MIDI_ENCODER_BASE_CC, ENCODER_COUNT, Control::new));
        midiControls.addAll(makeControls(MIDI_FADER_CHANNEL, MIDI_FADER_BASE_CC, FADER_COUNT, Control::new));
    }

    public List<Control> midiControls() {
        return midiControls;
    }

    public List<Control> mixerButtons() {
        return mixerButtons;
    }

    public List<Encoder> mixerEncoders() {
        return mixerEncoders;
    }

    public List<Fader> mixerFaders() {
        return mixerFaders;
    }

    public Control playButton() {
        return playButton;
    }

    public Control stopButton() {
        return stopButton;
    }

    public Control rewindButton() {
        return rewindButton;
    }

    public Control fastForwardButton() {
        return fastForwardButton;
    }

    public Control recordButton() {
        return recordButton;
    }

    public Control loopButton() {
        return loopButton;
    }

    public Toggle faderMixerModeButton() {
        return faderMixerModeButton;
    }

    public Toggle faderMidiModeButton() {
        return faderMidiModeButton;
    }

    public Toggle encoderMidiModeButton() {
        return encoderMidiModeButton;
    }

    public Toggle encoderMixerModeButton() {
        return encoderMixerModeButton;
    }

    public Toggle encoderPluginModeButton() {
        return encoderPluginModeButton;
    }

    private static <T> T makeControl(int channel, int cc, Function<ControlIdentifier, T> controlBuilder) {
        return controlBuilder.apply(new ControlIdentifier(channel, cc));
    }

    private static <T> List<T> makeControls(int channel, int baseCC, int count, Function<ControlIdentifier, T> controlBuilder) {
        return IntStream.range(0, count)
                .mapToObj(index -> makeControl(channel, baseCC + index, controlBuilder))
                .collect(Collectors.toList());
    }

    private static String sysexMessage(String content) {
        return String.format(MESSAGE_FORMAT, content);
    }
}
