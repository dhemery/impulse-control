package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.bitwig.ParameterSetterMode;
import com.dhemery.impulse.Encoder;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class EncoderBankController implements Consumer<Mode> {
    private static final double REMOTE_CONTROL_STEP_SIZE = 0.01;
    private static final double PAN_STEP_SIZE = 0.005;
    private static final Function<Integer, Double> ENCODER_VALUE_TO_PAN_INCREMENT = v -> PAN_STEP_SIZE * Encoder.steps(v);
    private static final Function<Integer, Double> ENCODER_VALUE_TO_REMOTE_CONTROL_INCREMENT = v -> REMOTE_CONTROL_STEP_SIZE * Encoder.steps(v);
    private static final BiConsumer<Parameter, Double> INCREMENT_PARAMETER_VALUE = SettableRangedValue::inc;
    private final Bitwig bitwig;
    private final String name;
    private Mode currentMode;

    public EncoderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        name = "Encoders";
        this.bitwig = bitwig;
        List<Parameter> panParameters = bitwig.channelFeatures(Channel::getPan);
        List<Parameter> remoteControls = bitwig.remoteControls();
        List<Encoder> encoders = impulse.mixerEncoders();

        Mode midiMode = new UninvocableMode("MIDI", this::debug);
        Mode mixerMode = new ParameterSetterMode("Channel Pan", panParameters, ENCODER_VALUE_TO_PAN_INCREMENT, INCREMENT_PARAMETER_VALUE);
        Mode pluginMode = new ParameterSetterMode("Remote Control", remoteControls, ENCODER_VALUE_TO_REMOTE_CONTROL_INCREMENT, INCREMENT_PARAMETER_VALUE);

        currentMode = midiMode;

        Runnable midiModeSetter = new SingletonModeSetter(this, midiMode);
        Runnable mixerModeSetter =  new SingletonModeSetter(this, mixerMode);
        Runnable pluginModeSetter =  new SingletonModeSetter(this, pluginMode);

        dispatcher.onTouch(impulse.encoderMidiModeButton(), midiModeSetter);
        dispatcher.onTouch(impulse.encoderMixerModeButton(), mixerModeSetter);
        dispatcher.onTouch(impulse.encoderPluginModeButton(), pluginModeSetter);

        IntStream.range(0, encoders.size())
                .forEach(i -> dispatcher.onValue(encoders.get(i), v -> currentMode.accept(i, v)));
    }

    private void debug(String message) {
        bitwig.debug(format("%s: %s", this, message));
    }

    @Override
    public void accept(Mode newMode) {
        if (currentMode == newMode) return;
        currentMode.exit();
        currentMode = newMode;
        currentMode.enter();
        bitwig.debug(format("%s %s", this, format("-> %s", currentMode)));
    }

    @Override
    public String toString() {
        return name;
    }
}
