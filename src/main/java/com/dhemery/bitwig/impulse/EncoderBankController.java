package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.bitwig.ParameterSetter;
import com.dhemery.impulse.Encoder;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class EncoderBankController {
    private static final double REMOTE_CONTROL_STEP_SIZE = 0.01;
    private static final double PAN_STEP_SIZE = 0.005;
    private static final Function<Integer, Double> ENCODER_VALUE_TO_PAN_INCREMENT = v -> PAN_STEP_SIZE * Encoder.steps(v);
    private static final Function<Integer, Double> ENCODER_VALUE_TO_REMOTE_CONTROL_INCREMENT = v -> REMOTE_CONTROL_STEP_SIZE * Encoder.steps(v);
    private static final BiConsumer<Parameter, ? super Double> INCREMENT_PARAMETER_VALUE = SettableRangedValue::inc;
    private final Bitwig bitwig;
    private final String name;
    private EncoderMode currentMode;

    public EncoderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        name = "Encoders";
        this.bitwig = bitwig;
        List<ParameterSetter> panSetters = bitwig.channelFeatures(Channel::getPan).stream()
                .map(p -> new ParameterSetter(p, ENCODER_VALUE_TO_PAN_INCREMENT, INCREMENT_PARAMETER_VALUE))
                .collect(Collectors.toList());
        List<ParameterSetter> remoteControlSetters = bitwig.remoteControls().stream()
                .map(p -> new ParameterSetter(p, ENCODER_VALUE_TO_REMOTE_CONTROL_INCREMENT, INCREMENT_PARAMETER_VALUE))
                .collect(Collectors.toList());
        List<Encoder> encoders = impulse.mixerEncoders();

        EncoderMode midiMode = new EncoderMode("MIDI");
        EncoderMode mixerMode = new EncoderMode("Channel Pan", panSetters);
        EncoderMode pluginMode = new EncoderMode("Remote Control", remoteControlSetters);

        currentMode = midiMode;

        dispatcher.onTouch(impulse.encoderMidiModeButton(), () -> enter(midiMode));
        dispatcher.onTouch(impulse.encoderMixerModeButton(), () -> enter(mixerMode));
        dispatcher.onTouch(impulse.encoderPluginModeButton(), () -> enter(pluginMode));

        IntStream.range(0, encoders.size())
                .forEach(i -> dispatcher.onValue(encoders.get(i), v -> currentMode.accept(i, v)));
    }

    private void enter(EncoderMode newMode) {
        currentMode.exit();
        currentMode = newMode;
        currentMode.enter();
    }

    private void debug(String message) {
        bitwig.debug(format("%s %s", this, message));
    }

    @Override
    public String toString() {
        return name;
    }

    private class EncoderMode implements BiConsumer<Integer, Integer> {
        private final String name;
        private final List<ParameterSetter> actions = new ArrayList<>();

        public EncoderMode(String name, List<ParameterSetter> actions) {
            this.name = name;
            this.actions.addAll(actions);
        }

        public EncoderMode(String name) {
            this(name, Collections.emptyList());
        }

        @Override
        public void accept(Integer index, Integer value) {
            actions.get(index).accept(value);
        }

        public void enter() {
            actions.forEach(ParameterSetter::activate);
            debug(format("-> %s", this));
        }

        public void exit() {
            actions.forEach(ParameterSetter::deactivate);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
