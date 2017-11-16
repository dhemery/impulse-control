package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeProcessor;

import java.util.List;

public class FaderBankController {
    private final List<Parameter> mixerModeParameters;
    private final Bitwig bitwig;

    public FaderBankController(Impulse impulse, Bitwig bitwig, ControlChangeProcessor dispatcher) {
        this.bitwig = bitwig;
        mixerModeParameters = bitwig.channelParameters(Channel::getVolume);
        dispatcher.register(impulse.faderMixerModeButton(), this::enterMixerMode);
        dispatcher.register(impulse.faderMidiModeButton(), this::enterMidiMode);
        setMixerMode(false);
    }

    private void enterMixerMode(int ignored) {
        setMixerMode(true);
        bitwig.status("Faders: Mixer Mode");
    }

    private void enterMidiMode(int ignored) {
        setMixerMode(false);
        bitwig.status("Faders: MIDI Mode");
    }

    private void setMixerMode(boolean isMixerMode) {
        mixerModeParameters.forEach(p -> p.setIndication(isMixerMode));
    }
}
