package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.bitwig.ParameterSetterMode;
import com.dhemery.impulse.Fader;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class FaderBankController {
    private static final BiConsumer<Parameter, Double> SET_PARAMETER_VALUE = SettableRangedValue::set;
    private static final Function<Integer, Double> FADER_VALUE_TO_VOLUME = sv -> (double) sv / Fader.MAX_VALUE;
    private final String name;
    private final Bitwig bitwig;
    private Mode currentMode;

    public FaderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        name = "Faders";
        this.bitwig = bitwig;
        List<Parameter> volumeParameters = bitwig.channelFeatures(Channel::getVolume);
        List<Fader> faders = impulse.mixerFaders();

        Mode midiMode = new Mode("MIDI");
        Mode mixerMode = new ParameterSetterMode("Channel Volume", volumeParameters, FADER_VALUE_TO_VOLUME, SET_PARAMETER_VALUE);
        currentMode = midiMode;

        dispatcher.onTouch(impulse.faderMidiModeButton(), () -> enter(midiMode));
        dispatcher.onTouch(impulse.faderMixerModeButton(), () -> enter(mixerMode));

        IntStream.range(0, faders.size())
                .forEach(i -> dispatcher.onValue(faders.get(i), v -> currentMode.accept(i, v)));
    }

    private void enter(Mode newMode) {
        if (currentMode == newMode) return;
        currentMode.exit();
        currentMode = newMode;
        currentMode.enter();
        bitwig.debug(format("%s %s", name, format("-> %s", currentMode)));
    }
}
