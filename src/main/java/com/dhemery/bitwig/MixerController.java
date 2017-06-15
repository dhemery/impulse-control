package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;
import com.dhemery.impulse.*;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;

public class MixerController {
    private static final int VOLUME_FADER_RESOLUTION = 128;
    // TODO: Add extension preference for pan sensitivity?
    // TODO: Shift button temporarily increases sensitivity?
    private static final int PAN_ENCODER_RESOLUTION = 201; // Range from -100% to 100% in 1% increments
    private final Display display;

    // TODO: Plugin mode sends encoder changes to the currently selected device's Remote Controls.
    public MixerController(ControllerHost host, Impulse impulse, ControlChangeDispatcher dispatcher, Display display) {
        this.display = display;
        List<MomentaryButton> buttons = impulse.mixerButtons();
        List<RotaryEncoder> encoders = impulse.mixerEncoders();
        List<LinearEncoder> faders = impulse.mixerFaders();
        TrackBank trackBank = host.createTrackBank(encoders.size(), 0, 0);
        for (int c = 0; c < trackBank.getSizeOfBank(); c++) {
            Track channel = trackBank.getChannel(c);
            connectChannelVolumeFader(dispatcher, channel, faders.get(c));
            connectChannelPanEncoder(dispatcher, channel, encoders.get(c));
        }
    }

    private void connectChannelPanEncoder(ControlChangeDispatcher dispatcher, Track channel, RotaryEncoder encoder) {
        Parameter channelPan = channel.getPan();
        channelPan.markInterested();
        channelPan.setIndication(true);
        dispatcher.register(encoder, v -> channelPan.inc(encoder.directionOf(v), PAN_ENCODER_RESOLUTION));
    }

    private void connectChannelVolumeFader(ControlChangeDispatcher dispatcher, Track channel, LinearEncoder fader) {
        Parameter channelVolume = channel.getVolume();
        channelVolume.markInterested();
        channelVolume.setIndication(true);
        dispatcher.register(fader, volume -> channelVolume.set(volume, VOLUME_FADER_RESOLUTION));
    }
}

