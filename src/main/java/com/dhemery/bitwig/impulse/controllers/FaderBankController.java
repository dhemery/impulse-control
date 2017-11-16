package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.Display;
import com.dhemery.impulse.controls.Fader;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeProcessor;

import java.util.List;

public class FaderBankController {
    private final List<Fader> faders;
    private final List<Parameter> mixerModeParameters;
    private final ControlChangeProcessor dispatcher;
    private final Display display;

    public FaderBankController(Impulse impulse, List<Parameter> mixerModeParameters, ControlChangeProcessor dispatcher, Display display) {
        this.faders = impulse.mixerFaders();
        this.mixerModeParameters = mixerModeParameters;
        this.dispatcher = dispatcher;
        this.display = display;
        dispatcher.register(impulse.faderMixerModeButton(), this::enterMixerMode);
        dispatcher.register(impulse.faderMidiModeButton(), this::enterMidiMode);
    }

    public void enterMixerMode(int value) {
        display.status("Faders: Mixer Mode");
        mixerModeParameters.forEach(p -> p.setIndication(true));
    }

    public void enterMidiMode(int value) {
        display.status("Faders: Midi Mode");
        mixerModeParameters.forEach(p -> p.setIndication(false));
    }
}
