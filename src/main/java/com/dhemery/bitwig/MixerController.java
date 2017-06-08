package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;
import com.dhemery.bitwig.commands.SelectorCommand;
import com.dhemery.bitwig.commands.StepperCommand;
import com.dhemery.midi.ControlChangeDispatcher;
import com.dhemery.midi.ControlIdentifier;

public class MixerController {
    private static final int TRACK_BANK_TRACK_COUNT = 8;
    private static final int ENCODER_BASE_CONTROL = 0x00;
    private static final int ENCODER_CHANNEL = 1;
    private static final int FADER_BASE_CONTROL = 0x00;
    private static final int FADER_CHANNEL = 0;
    private static final int VOLUME_FADER_RESOLUTION = 128;
    // TODO: Pan sensitivity preference?
    // TODO: Shift button temporarily increases sensitivity?
    private static final int PAN_ENCODER_RESOLUTION = 201; // Range from -100% to 100% in 1% increments
    private final TrackBank trackBank;
    private final Display display;

    // TODO: Plugin mode sends encoder changes to the currently selected device's Remote Controls.
    public MixerController(ControllerHost host, ControlChangeDispatcher dispatcher, Display display) {
        this.display = display;
        trackBank = host.createTrackBank(8, 1, 1);
        for (int channelIndex = 0; channelIndex < TRACK_BANK_TRACK_COUNT; channelIndex++) {
            Track channel = trackBank.getChannel(channelIndex);
            connectChannelVolumeFader(dispatcher, channelIndex, channel);
            connectChannelPanEncoder(dispatcher, channelIndex, channel);
        }
    }

    private void connectChannelPanEncoder(ControlChangeDispatcher dispatcher, int channelIndex, Track channel) {
        Parameter channelPan = channel.getPan();
        channelPan.markInterested();
        dispatcher.register(encoderIdentifier(channelIndex),  new StepperCommand(step -> channelPan.inc(step, PAN_ENCODER_RESOLUTION)));
    }

    private void connectChannelVolumeFader(ControlChangeDispatcher dispatcher, int channelIndex, Track channel) {
        Parameter channelVolume = channel.getVolume();
        channelVolume.markInterested();
        dispatcher.register(faderIdentifier(channelIndex),  new SelectorCommand(volume -> channelVolume.set(volume, VOLUME_FADER_RESOLUTION)));
    }

    private static ControlIdentifier faderIdentifier(int faderIndex) {
        return new ControlIdentifier(FADER_CHANNEL, faderIndex + FADER_BASE_CONTROL);
    }

    private static ControlIdentifier encoderIdentifier(int encoderIndex) {
        return new ControlIdentifier(ENCODER_CHANNEL, encoderIndex + ENCODER_BASE_CONTROL);
    }
}

