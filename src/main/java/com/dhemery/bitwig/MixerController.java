package com.dhemery.bitwig;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.*;
import com.dhemery.midi.Control;
import com.dhemery.midi.ControlChangeDispatcher;

public class MixerController {
    private static final int TRACK_BANK_TRACK_COUNT = 8;
    private static final int FADER_CHANNEL = 0;
    private static final int FADER_BASE_CONTROL = 0x00;
    private static final int FADER_RANGE = 128;
    private final TrackBank trackBank;

    public MixerController(ControllerHost host, ControlChangeDispatcher dispatcher) {
        trackBank = host.createTrackBank(8, 1, 1);
        for(int trackIndex = 0 ; trackIndex < TRACK_BANK_TRACK_COUNT ; trackIndex++) {
            Track channel = trackBank.getChannel(trackIndex);
            channel.getVolume().markInterested();
        }
        for(int faderIndex = 0 ; faderIndex < TRACK_BANK_TRACK_COUNT ; faderIndex++) {
            dispatcher.register(new Control(FADER_CHANNEL, faderCC(faderIndex)), this::onFaderChange);
        }
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
}

