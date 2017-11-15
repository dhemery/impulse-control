package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;
import com.dhemery.impulse.Control;
import com.dhemery.impulse.StepperEncoder;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;

public class MixerController {
    public MixerController(TrackBank trackBank, List<Control> faders, List<StepperEncoder> encoders, ControlChangeDispatcher dispatcher) {
        for (int c = 0; c < trackBank.getSizeOfBank(); c++) {
            Track channel = trackBank.getChannel(c);
            connectChannelVolumeFader(dispatcher, channel, faders.get(c));
            connectChannelPanEncoder(dispatcher, channel, encoders.get(c));
        }
    }

    private static final int PAN_ENCODER_RESOLUTION = 201; // Range from -100% to 100% in 1% increments
    private void connectChannelPanEncoder(ControlChangeDispatcher dispatcher, Track channel, StepperEncoder encoder) {
        Parameter channelPan = channel.getPan();
        channelPan.markInterested();
        channelPan.setIndication(true);
        dispatcher.register(encoder, v -> channelPan.inc(encoder.directionOf(v), PAN_ENCODER_RESOLUTION));
    }

    private static final int VOLUME_FADER_RESOLUTION = 128;
    private void connectChannelVolumeFader(ControlChangeDispatcher dispatcher, Track channel, Control fader) {
        Parameter channelVolume = channel.getVolume();
        channelVolume.markInterested();
        channelVolume.setIndication(true);
        dispatcher.register(fader, volume -> channelVolume.set(volume, VOLUME_FADER_RESOLUTION));
    }
}

