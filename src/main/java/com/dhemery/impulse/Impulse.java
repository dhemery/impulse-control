package com.dhemery.impulse;

import com.bitwig.extension.controller.api.MidiOut;

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

    private final List<ToggleButton> midiButtons = makeControls(MIDI_BUTTON_CHANNEL, MIDI_BUTTON_BASE_CC, BUTTON_COUNT, ToggleButton::new);
    private final List<Control> midiEncoders = makeControls(MIDI_ENCODER_CHANNEL, MIDI_ENCODER_BASE_CC, ENCODER_COUNT, Control::new);
    private final List<Control> midiFaders = makeControls(MIDI_FADER_CHANNEL, MIDI_FADER_BASE_CC, FADER_COUNT, Control::new);
    private final List<MomentaryButton> mixerButtons = makeControls(MIXER_BUTTON_CHANNEL, MIXER_BUTTON_BASE_CC, BUTTON_COUNT, MomentaryButton::new);
    private final List<StepperEncoder> mixerEncoders = makeControls(MIXER_ENCODER_CHANNEL, MIXER_ENCODER_BASE_CC, ENCODER_COUNT, StepperEncoder::new);
    private final List<Control> mixerFaders = makeControls(MIXER_FADER_CHANNEL, MIXER_FADER_BASE_CC, FADER_COUNT, Control::new);
    private final MomentaryButton playButton = makeControl(TRANSPORT_CC_CHANNEL, PLAY_BUTTON_CC, MomentaryButton::new);
    private final MomentaryButton stopButton = makeControl(TRANSPORT_CC_CHANNEL, STOP_BUTTON_CC, MomentaryButton::new);
    private final MomentaryButton rewindButton = makeControl(TRANSPORT_CC_CHANNEL, REWIND_BUTTON_CC, MomentaryButton::new);
    private final MomentaryButton fastForwardButton = makeControl(TRANSPORT_CC_CHANNEL, FAST_FORWARD_BUTTON_CC, MomentaryButton::new);
    private final MomentaryButton recordButton = makeControl(TRANSPORT_CC_CHANNEL, RECORD_BUTTON_CC, MomentaryButton::new);
    private final MomentaryButton loopButton = makeControl(TRANSPORT_CC_CHANNEL, LOOP_BUTTON_CC, MomentaryButton::new);

    public Impulse(MidiOut port) {
        port.sendSysex(CONNECT_TO_COMPUTER);
    }

    public List<ToggleButton> midiButtons() {
        return midiButtons;
    }

    public List<Control> midiEncoders() {
        return midiEncoders;
    }

    public List<Control> midiFaders() {
        return midiFaders;
    }

    public List<MomentaryButton> mixerButtons() {
        return mixerButtons;
    }

    public List<StepperEncoder> mixerEncoders() {
        return mixerEncoders;
    }

    public List<Control> mixerFaders() { return mixerFaders; }

    public MomentaryButton playButton() {
        return playButton;
    }

    public MomentaryButton stopButton() {
        return stopButton;
    }

    public MomentaryButton rewindButton() {
        return rewindButton;
    }

    public MomentaryButton fastForwardButton() {
        return fastForwardButton;
    }

    public MomentaryButton recordButton() {
        return recordButton;
    }

    public MomentaryButton loopButton() {
        return loopButton;
    }

    private static String sysexMessage(String content) {
        return String.format(MESSAGE_FORMAT, content);
    }


    private static <T> T makeControl(int channel, int cc, Function<ControlIdentifier, T> controlBuilder) {
        return controlBuilder.apply(new ControlIdentifier(channel, cc));
    }

    private static <T> List<T> makeControls(int channel, int baseCC, int count, Function<ControlIdentifier, T> controlBuilder) {
        return IntStream.range(0, count)
                .mapToObj(index -> makeControl(channel, baseCC+index, controlBuilder))
                .collect(Collectors.toList());
    }
}
