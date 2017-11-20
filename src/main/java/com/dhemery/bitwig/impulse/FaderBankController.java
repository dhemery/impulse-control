package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.bitwig.ParameterSetter;
import com.dhemery.impulse.Fader;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class FaderBankController {
    private static final BiConsumer<Parameter, Double> SET_PARAMETER_VALUE = SettableRangedValue::set;
    private static final Function<Integer, Double> FADER_VALUE_TO_VOLUME = sv -> (double) sv / Fader.MAX_VALUE;
    private final String name;
    private final Bitwig bitwig;
    private FaderMode currentMode;

    public FaderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        name = "Faders";
        this.bitwig = bitwig;
        List<ParameterSetter> volumeSetters= bitwig.channelFeatures(Channel::getVolume).stream()
                .map(p -> new ParameterSetter(p, FADER_VALUE_TO_VOLUME, SET_PARAMETER_VALUE))
                .collect(toList());
        List<Fader> faders = impulse.mixerFaders();

        FaderMode midiMode = new FaderMode("MIDI");
        FaderMode mixerMode = new FaderMode("Channel Volume", volumeSetters);
        currentMode = midiMode;

        dispatcher.onTouch(impulse.faderMidiModeButton(), () -> enter(midiMode));
        dispatcher.onTouch(impulse.faderMixerModeButton(), () -> enter(mixerMode));

        IntStream.range(0, faders.size())
                .forEach(i -> dispatcher.onValue(faders.get(i), v -> currentMode.accept(i, v)));
    }

    private void enter(FaderMode newMode) {
        if(currentMode == newMode) return;
        currentMode.exit();
        currentMode = newMode;
        currentMode.enter();
    }

    private void debug(String message) {
        bitwig.debug(format("%s %s", name, message));
    }

    private class FaderMode implements BiConsumer<Integer, Integer> {
        private final String name;
        private final List<ParameterSetter> actions = new ArrayList<>();

        public FaderMode(String name, List<ParameterSetter> actions) {
            this.actions.addAll(actions);
            this.name = name;
        }

        public FaderMode(String name) {
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
