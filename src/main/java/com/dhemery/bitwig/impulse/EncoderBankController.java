package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Encoder;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ObjIntConsumer;

import static java.lang.String.format;

public class EncoderBankController {
    private static final double REMOTE_CONTROL_STEP_SIZE = 0.01;
    private static final double PAN_STEP_SIZE = 0.005;
    private final Bitwig bitwig;
    private final String name;
    private EncoderMode currentMode;

    public EncoderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        name = "Encoders";
        this.bitwig = bitwig;
        List<Parameter> panParameters = bitwig.channelFeatures(Channel::getPan);
        List<Parameter> remoteControls = bitwig.remoteControls();
        List<Encoder> encoders = impulse.mixerEncoders();

        EncoderMode midiMode = new EncoderMode("MIDI");
        EncoderMode mixerMode = new EncoderMode("Channel Pan", encoders, panParameters, PAN_STEP_SIZE);
        EncoderMode pluginMode = new EncoderMode("Remote Control", encoders, remoteControls, REMOTE_CONTROL_STEP_SIZE);

        currentMode = midiMode;

        dispatcher.onTouch(impulse.encoderMidiModeButton(), () -> enter(midiMode));
        dispatcher.onTouch(impulse.encoderMixerModeButton(), () -> enter(mixerMode));
        dispatcher.onTouch(impulse.encoderPluginModeButton(), () -> enter(pluginMode));

        encoders.forEach(c -> dispatcher.onValue(c, this::onControlChange));
    }

    private void enter(EncoderMode newMode) {
        currentMode.exit();
        currentMode = newMode;
        currentMode.enter();
    }

    private void onControlChange(Encoder encoder, int value) {
        currentMode.accept(encoder, value);
    }

    private void debug(String message) {
        bitwig.debug(format("%s %s", this, message));
    }

    @Override
    public String toString() {
        return name;
    }

    private class EncoderMode implements ObjIntConsumer<Encoder> {
        private final String name;
        private final double scale;
        private final Map<Encoder, Parameter> parametersByEncoder = new HashMap<>();

        public EncoderMode(String name, List<Encoder> encoders, List<Parameter> parameters, double scale) {
            this.name = name;
            this.scale = scale;
            for(int i = 0 ; i < parameters.size(); i++) parametersByEncoder.put(encoders.get(i), parameters.get(i));
        }

        public EncoderMode(String name) {
            this(name, Collections.emptyList(), Collections.emptyList(), 1);
        }

        @Override
        public void accept(Encoder encoder, int value) {
            Parameter parameter = parametersByEncoder.get(encoder);
            parameter.inc(scale * (double) encoder.size(value));
        }

        public void enter() {
            parametersByEncoder.values().forEach(p -> p.setIndication(true));
            debug(format("-> %s", this));
        }

        public void exit() {
            parametersByEncoder.values().forEach(p -> p.setIndication(false));
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
