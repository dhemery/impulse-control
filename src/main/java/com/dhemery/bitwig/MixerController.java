package com.dhemery.bitwig;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.*;
import com.dhemery.midi.ControlIdentifier;
import com.dhemery.midi.ControlChangeDispatcher;

public class MixerController {
    private static final int TRACK_BANK_TRACK_COUNT = 8;
    private static final int ENCODER_BASE_CONTROL = 0x00;
    private static final int ENCODER_BASE_VALUE = 0x40;
    private static final int ENCODER_CHANNEL = 1;
    private static final int FADER_BASE_CONTROL = 0x00;
    private static final int FADER_CHANNEL = 0;
    private static final int FADER_RANGE = 128;
    // TODO: Pan sensitivity preference?
    // TODO: Shift button temporarily increases sensitivity?
    private static final int PAN_RESOLUTION = 201; // 1% increments from -100% to 100%
    private final TrackBank trackBank;
    private final Display display;

    public MixerController(ControllerHost host, ControlChangeDispatcher dispatcher, Display display) {
        this.display = display;
        trackBank = host.createTrackBank(8, 1, 1);
        for(int trackIndex = 0 ; trackIndex < TRACK_BANK_TRACK_COUNT ; trackIndex++) {
            Track channel = trackBank.getChannel(trackIndex);
            channel.getVolume().markInterested();
            channel.getPan().markInterested();
        }
        for(int faderIndex = 0 ; faderIndex < TRACK_BANK_TRACK_COUNT ; faderIndex++) {
            dispatcher.register(new ControlIdentifier(FADER_CHANNEL, faderCC(faderIndex)), this::onFaderChange);
        }

        // TODO: Plugin mode sends encoder changes to the currently selected device's Remote Controls.
        for(int encoderIndex = 0 ; encoderIndex < TRACK_BANK_TRACK_COUNT ; encoderIndex++) {
            dispatcher.register(new ControlIdentifier(ENCODER_CHANNEL, encoderCC(encoderIndex)), this::onEncoderChange);
        }
    }

    private void onEncoderChange(ShortMidiMessage message) {
        Track track = encoderTrack(message.getData1());
        int panAmount = message.getData2() - ENCODER_BASE_VALUE;
        track.getPan().inc(panAmount, PAN_RESOLUTION);
    }

    private void onFaderChange(ShortMidiMessage message) {
        Track track = faderTrack(message.getData1());
        track.getVolume().set(message.getData2(), FADER_RANGE);
    }

    private Track faderTrack(int faderCC) {
        return trackBank.getChannel(faderCC - FADER_BASE_CONTROL);
    }

    private int faderCC(int faderIndex) {
        return faderIndex + FADER_BASE_CONTROL;
    }

    private Track encoderTrack(int encoderCC) {
        return trackBank.getChannel(encoderCC - ENCODER_BASE_CONTROL);
    }

    private static int encoderCC(int encoderIndex) {
        return encoderIndex + ENCODER_BASE_CONTROL;
    }
}

