package com.dhemery.bitwig;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.*;
import com.dhemery.midi.Control;
import com.dhemery.midi.ControlChangeDispatcher;

public class MixerController {
    private static final int FADER_CHANNEL = 0;
    private static final int FADER_COUNT = 8;
    private static final int FADER_BASE_CONTROL = 0x00;
    private static final int FADER_RANGE = 128;
    private final TrackBank trackBank;

    public MixerController(ControllerHost host, ControlChangeDispatcher dispatcher) {
        trackBank = host.createTrackBank(8, 1, 1);
        for(int i = 0 ; i < FADER_COUNT ; i++) {
            Track channel = trackBank.getChannel(i);
            channel.getVolume().markInterested();
            dispatcher.register(new Control(FADER_CHANNEL, FADER_BASE_CONTROL+i), this::onFaderChange);
        }
    }

    private void onFaderChange(ShortMidiMessage message) {
        Track track = faderTrack(message.getData1());
        track.getVolume().set(message.getData2(), FADER_RANGE);
    }

    private Track faderTrack(int ccNumber) {
        return trackBank.getChannel(ccNumber - FADER_BASE_CONTROL);
    }
}

