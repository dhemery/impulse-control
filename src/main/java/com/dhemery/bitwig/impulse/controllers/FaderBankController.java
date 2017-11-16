package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.controls.Fader;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class FaderBankController {
    private final Bitwig bitwig;
    private final Map<Fader, Parameter> parametersByCC = new HashMap<>();

    public FaderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        this.bitwig = bitwig;
        List<Parameter> channelVolumeParameters = bitwig.channelParameters(Channel::getVolume);
        List<Fader> mixerFaders = impulse.mixerFaders();
        IntStream.range(0, channelVolumeParameters.size())
                .forEach(i -> parametersByCC.put(mixerFaders.get(i), channelVolumeParameters.get(i)));
        mixerFaders.forEach(f -> dispatcher.onValue(f, this::setChannelVolume));

        dispatcher.onTouch(impulse.faderMixerModeButton(), this::enterMixerMode);
        dispatcher.onTouch(impulse.faderMidiModeButton(), this::enterMidiMode);

        enterMidiMode();
    }

    private void setChannelVolume(Fader fader, int value) {
        parametersByCC.get(fader).set(fader.normalize(value));
    }

    private void enterMixerMode() {
        setMixerMode(true);
        bitwig.status("Faders: Mixer Mode");
    }

    private void enterMidiMode() {
        setMixerMode(false);
        bitwig.status("Faders: MIDI Mode");
    }

    private void setMixerMode(boolean isMixerMode) {
        parametersByCC.values().forEach(p -> p.setIndication(isMixerMode));
    }
}
