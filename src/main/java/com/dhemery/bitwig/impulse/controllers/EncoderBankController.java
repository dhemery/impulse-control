package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.controls.Encoder;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class EncoderBankController {
    private final ControlMode mixerMode;
    private final ControlMode midiMode;
    private final ControlMode pluginMode;
    private ControlMode currentMode;

    public EncoderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        List<Parameter> channelPanParameters = bitwig.channelParameters(Channel::getPan);
        List<Parameter> remoteControls = bitwig.remoteControls();

        List<Encoder> mixerEncoders = impulse.mixerEncoders();
        mixerEncoders.forEach(f -> dispatcher.onValue(f, this::forwardToMode));

        mixerMode = new ControlMode<>("Encoder Mixer", bitwig, mixerEncoders, channelPanParameters, SettableRangedValue::inc, 0.005);
        pluginMode = new ControlMode<>("Encoder Plugin", bitwig, mixerEncoders, remoteControls, SettableRangedValue::inc, 0.01);
        midiMode = new ControlMode<>("Encoder MIDI", bitwig, Collections.emptyList(), Collections.emptyList(), SettableRangedValue::inc, 1.0);

        currentMode = midiMode;

        dispatcher.onTouch(impulse.encoderMidiModeButton(), () -> enter(midiMode));
        dispatcher.onTouch(impulse.encoderMixerModeButton(), () -> enter(mixerMode));
        dispatcher.onTouch(impulse.encoderPluginModeButton(), () -> enter(pluginMode));
    }

    private void enter(ControlMode mode) {
        currentMode.exit();
        currentMode = mode;
        currentMode.enter();
    }

    private void forwardToMode(Encoder encoder, int value) {
        currentMode.accept(encoder, value);
    }
}
