package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.controls.Encoder;
import com.dhemery.midi.ControlChangeProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EncoderBankController {
    private final List<Parameter> channelPanParameters;
    private final List<Parameter> remoteControls;
    private final Bitwig bitwig;
    private final List<Encoder> mixerEncoders;
    private final Map<Integer, Parameter> parametersByCC = new HashMap<>();
    private final Map<Integer, Parameter> remoteControlsByCC = new HashMap<>();
    private List<Parameter> activeParametersByCC;
    private int activeParameterScale;

    public EncoderBankController(Impulse impulse, Bitwig bitwig, ControlChangeProcessor dispatcher) {
        this.bitwig = bitwig;
        channelPanParameters = bitwig.channelParameters(Channel::getPan);
        remoteControls = bitwig.remoteControls();

        mixerEncoders = impulse.mixerEncoders();
        IntStream.range(0, channelPanParameters.size())
                .forEach(i -> parametersByCC.put(mixerEncoders.get(i).identifier.cc, channelPanParameters.get(i)));
        IntStream.range(0, remoteControls.size())
                .forEach(i -> remoteControlsByCC.put(mixerEncoders.get(i).identifier.cc, remoteControls.get(i)));
        mixerEncoders.forEach(f -> dispatcher.register(f, this::handleEncoderChange));

        dispatcher.register(impulse.encoderMidiModeButton(), this::enterMidiMode);
        dispatcher.register(impulse.encoderMixerModeButton(), this::enterMixerMode);
        dispatcher.register(impulse.encoderPluginModeButton(), this::enterPluginMode);

        enterMixerMode(0, 0);
    }

    public void enterMixerMode(int ignoredCC, int ignoredValue) {
        remoteControls.forEach(p -> p.setIndication(false));
        channelPanParameters.forEach(p -> p.setIndication(true));
        activeParametersByCC = channelPanParameters;
        activeParameterScale = 201;
        bitwig.status("Encoders: Mixer Mode");
    }

    public void enterPluginMode(int ignoredCC, int ignoredValue) {
        channelPanParameters.forEach(p -> p.setIndication(false));
        remoteControls.forEach(p -> p.setIndication(true));
        activeParametersByCC = remoteControls;
        activeParameterScale = 101;
        bitwig.status("Encoders: Plugin Mode");
    }

    public void enterMidiMode(int ignoredCC, int ignoredValue) {
        channelPanParameters.forEach(p -> p.setIndication(false));
        remoteControls.forEach(p -> p.setIndication(false));
        bitwig.status("Encoders: MIDI Mode");
    }

    private void handleEncoderChange(int cc, int value) {
        activeParametersByCC.get(cc).inc(value - 0x40, activeParameterScale);
    }
}
