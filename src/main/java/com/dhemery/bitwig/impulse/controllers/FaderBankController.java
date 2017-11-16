package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.controls.Fader;
import com.dhemery.midi.ControlChangeProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class FaderBankController {
    private final List<Parameter> channelVolumeParameters;
    private final Bitwig bitwig;
    private final List<Fader> mixerFaders;
    private final Map<Integer,Parameter> parametersByCC = new HashMap<>();

    public FaderBankController(Impulse impulse, Bitwig bitwig, ControlChangeProcessor dispatcher) {
        this.bitwig = bitwig;
        channelVolumeParameters = bitwig.channelParameters(Channel::getVolume);
        mixerFaders = impulse.mixerFaders();
        IntStream.range(0, channelVolumeParameters.size())
                .forEach(i -> parametersByCC.put(mixerFaders.get(i).identifier.cc, channelVolumeParameters.get(i)));
        mixerFaders.forEach(f -> dispatcher.register(f, this::setChannelVolume));

        dispatcher.register(impulse.faderMixerModeButton(), this::enterMixerMode);
        dispatcher.register(impulse.faderMidiModeButton(), this::enterMidiMode);

        setMixerMode(false);
    }

    private void setChannelVolume(int cc, int value) {
        parametersByCC.get(cc).set(value, 127);
    }

    private void enterMixerMode(int ignoredCC, int ignoredValue) {
        setMixerMode(true);
        bitwig.status("Faders: Mixer Mode");
    }

    private void enterMidiMode(int ignoredCC, int ignoredValue) {
        setMixerMode(false);
        bitwig.status("Faders: MIDI Mode");
    }

    private void setMixerMode(boolean isMixerMode) {
        channelVolumeParameters.forEach(p -> p.setIndication(isMixerMode));
    }
}
