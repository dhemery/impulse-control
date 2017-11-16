package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.controls.Control;
import com.dhemery.impulse.controls.Encoder;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.Collections;
import java.util.List;

public class EncoderBankController extends ControlBankController<Encoder> {

    public EncoderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        super("Encoder", bitwig, dispatcher, impulse.encoderMidiModeButton(), impulse.mixerEncoders(), SettableRangedValue::inc);

        List<Parameter> panParameters = bitwig.channelParameters(Channel::getPan);
        List<Parameter> remoteControls = bitwig.remoteControls();

        addMode("Mixer", impulse.encoderMixerModeButton(), panParameters, 0.005);
        addMode("Plugin", impulse.encoderPluginModeButton(), remoteControls, 0.01);
    }
}
