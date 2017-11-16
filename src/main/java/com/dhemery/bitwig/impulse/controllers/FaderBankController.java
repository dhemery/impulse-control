package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.controls.Fader;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;

public class FaderBankController extends ControlBankController<Fader> {
    public FaderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        super("Fader", bitwig, dispatcher, impulse.faderMidiModeButton(), impulse.mixerFaders(), SettableRangedValue::set);

        List<Parameter> channelVolumeParameters = bitwig.channelParameters(Channel::getVolume);

        addMode("Mixer", impulse.faderMixerModeButton(), channelVolumeParameters, 1);
    }
}