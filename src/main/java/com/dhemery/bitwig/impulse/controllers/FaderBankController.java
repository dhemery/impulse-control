package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.controls.Fader;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.Collections;
import java.util.List;

public class FaderBankController {
    private ControlMode<Fader> currentMode;

    public FaderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        List<Parameter> channelVolumeParameters = bitwig.channelParameters(Channel::getVolume);
        List<Fader> mixerFaders = impulse.mixerFaders();
        ControlMode<Fader> mixerMode = new ControlMode<>("Fader Mixer", bitwig, mixerFaders, channelVolumeParameters, SettableRangedValue::set, 1);
        ControlMode<Fader> midiMode = new ControlMode<>("Fader MIDI", bitwig, Collections.emptyList(), Collections.emptyList(), SettableRangedValue::set, 1);

        mixerFaders.forEach(f -> dispatcher.onValue(f, this::forwardToMode));

        dispatcher.onTouch(impulse.faderMixerModeButton(), () -> enter(mixerMode));
        dispatcher.onTouch(impulse.faderMidiModeButton(), () -> enter(midiMode));

        currentMode = midiMode;
        currentMode.enter();
    }

    private void forwardToMode(Fader fader, int value) {
        currentMode.accept(fader, value);
    }

    private void enter(ControlMode<Fader> mode) {
        currentMode.exit();
        currentMode = mode;
        currentMode.enter();
    }

}
