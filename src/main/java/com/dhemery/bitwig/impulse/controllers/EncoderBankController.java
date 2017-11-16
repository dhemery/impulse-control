package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.RemoteControl;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.bitwig.Display;
import com.dhemery.impulse.controls.Encoder;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeProcessor;

import java.util.List;

public class EncoderBankController {
    private final List<Parameter> mixerModeParameters;
    private final List<RemoteControl> pluginModeParameters;
    private final Impulse impulse;
    private final Bitwig bitwig;
    private final ControlChangeProcessor dispatcher;

    public EncoderBankController(Impulse impulse, Bitwig bitwig, ControlChangeProcessor dispatcher) {
        this.impulse = impulse;
        this.bitwig = bitwig;
        this.dispatcher = dispatcher;
        mixerModeParameters = bitwig.channelParameters(Channel::getPan);
        pluginModeParameters = bitwig.remoteControls();
        dispatcher.register(impulse.encoderMidiModeButton(), this::enterMidiMode);
        dispatcher.register(impulse.encoderMixerModeButton(), this::enterMixerMode);
        dispatcher.register(impulse.encoderPluginModeButton(), this::enterPluginMode);
    }

    public void enterMixerMode(int ignoredCC, int ignoredValue) {
        pluginModeParameters.forEach(p -> p.setIndication(false));
        mixerModeParameters.forEach(p -> p.setIndication(true));
        bitwig.status("Encoders: Mixer Mode");
    }

    public void enterPluginMode(int ignoredCC, int ignoredValue) {
        mixerModeParameters.forEach(p -> p.setIndication(false));
        pluginModeParameters.forEach(p -> p.setIndication(true));
        bitwig.status("Encoders: Plugin Mode");
    }

    public void enterMidiMode(int ignoredCC, int ignoredValue) {
        mixerModeParameters.forEach(p -> p.setIndication(false));
        pluginModeParameters.forEach(p -> p.setIndication(false));
        bitwig.status("Encoders: MIDI Mode");
    }
}
