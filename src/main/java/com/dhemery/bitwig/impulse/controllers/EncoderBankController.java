package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.Display;
import com.dhemery.impulse.controls.Encoder;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeProcessor;

import java.util.List;

public class EncoderBankController {
    private final List<Encoder> encoders;
    private final List<Parameter> mixerModeParameters;
    private final List<Parameter> pluginModeParameters;
    private final ControlChangeProcessor dispatcher;
    private final Impulse impulse;
    private final Display display;

    public EncoderBankController(Impulse impulse, List<Parameter> mixerModeParameters, List<Parameter> pluginModeParameters, ControlChangeProcessor dispatcher, Display display) {
        this.impulse = impulse;
        this.display = display;
        this.encoders = impulse.mixerEncoders();
        this.mixerModeParameters = mixerModeParameters;
        this.pluginModeParameters = pluginModeParameters;
        this.dispatcher = dispatcher;
        dispatcher.register(impulse.encoderMidiModeButton(), this::enterMidiMode);
        dispatcher.register(impulse.encoderMixerModeButton(), this::enterMixerMode);
        dispatcher.register(impulse.encoderPluginModeButton(), this::enterPluginMode);
    }

    public void enterMixerMode(int value) {
        display.status("Encoders: Mixer Mode");
        pluginModeParameters.forEach(p -> p.setIndication(false));
        mixerModeParameters.forEach(p -> p.setIndication(true));
    }

    public void enterPluginMode(int value) {
        display.status("Encoders: Plugin Mode");
        mixerModeParameters.forEach(p -> p.setIndication(false));
        pluginModeParameters.forEach(p -> p.setIndication(true));
    }

    public void enterMidiMode(int value) {
        display.status("Encoders: Midi Mode");
        mixerModeParameters.forEach(p -> p.setIndication(false));
        pluginModeParameters.forEach(p -> p.setIndication(false));
    }
}
