package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.controls.Encoder;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EncoderBankController {
    private static final Map<Encoder, Parameter> NO_ENCODER_MAPPINGS = Collections.emptyMap();
    private final Map<Encoder, Parameter> panParametersByEncoder = new HashMap<>();
    private final Map<Encoder, Parameter> remoteControlsByEncoder = new HashMap<>();
    private final Bitwig bitwig;
    private Map<Encoder, Parameter> activeParametersByEncoder = NO_ENCODER_MAPPINGS;
    private int activeParameterScale;

    public EncoderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        this.bitwig = bitwig;
        List<Parameter> channelPanParameters = bitwig.channelParameters(Channel::getPan);
        List<Parameter> remoteControls = bitwig.remoteControls();

        List<Encoder> mixerEncoders = impulse.mixerEncoders();
        IntStream.range(0, channelPanParameters.size())
                .forEach(i -> panParametersByEncoder.put(mixerEncoders.get(i), channelPanParameters.get(i)));
        IntStream.range(0, remoteControls.size())
                .forEach(i -> remoteControlsByEncoder.put(mixerEncoders.get(i), remoteControls.get(i)));
        mixerEncoders.forEach(f -> dispatcher.onValue(f, this::adjustParameter));

        dispatcher.onTouch(impulse.encoderMidiModeButton(), this::enterMidiMode);
        dispatcher.onTouch(impulse.encoderMixerModeButton(), this::enterMixerMode);
        dispatcher.onTouch(impulse.encoderPluginModeButton(), this::enterPluginMode);

        enterMidiMode();
    }

    public void enterMixerMode() {
        activate(panParametersByEncoder);
        activeParameterScale = 201;
        bitwig.status("Encoders: Mixer Mode");
    }

    public void enterPluginMode() {
        activate(remoteControlsByEncoder);
        activeParameterScale = 101;
        bitwig.status("Encoders: Plugin Mode");
    }

    public void enterMidiMode() {
        activate(NO_ENCODER_MAPPINGS);
        bitwig.status("Encoders: MIDI Mode");
    }

    private void activate(Map<Encoder, Parameter> arrivingParametersByCC) {
        activeParametersByEncoder.values().forEach(p -> p.setIndication(false));
        activeParametersByEncoder = arrivingParametersByCC;
        activeParametersByEncoder.values().forEach(p -> p.setIndication(true));
    }

    private void adjustParameter(Encoder encoder, int value) {
        activeParametersByEncoder.get(encoder).inc(value - 0x40, activeParameterScale);
    }
}
