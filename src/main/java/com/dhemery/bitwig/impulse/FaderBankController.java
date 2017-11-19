package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Parameter;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Fader;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ObjIntConsumer;

import static java.lang.String.format;

public class FaderBankController {
    private final String name;
    private final Bitwig bitwig;
    private FaderMode currentMode;

    public FaderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        name = "Faders";
        this.bitwig = bitwig;
        List<Parameter> panParameters = bitwig.channelFeatures(Channel::getVolume);
        List<Fader> faders = impulse.mixerFaders();

        FaderMode midiMode = new FaderMode("MIDI");
        FaderMode mixerMode = new FaderMode("Channel Volume", faders, panParameters);
        currentMode = midiMode;

        dispatcher.onTouch(impulse.faderMidiModeButton(), () -> enter(midiMode));
        dispatcher.onTouch(impulse.faderMixerModeButton(), () -> enter(mixerMode));

        faders.forEach(c -> dispatcher.onValue(c, this::onControlChange));
    }

    private void enter(FaderMode newMode) {
        if(currentMode == newMode) return;
        currentMode.exit();
        currentMode = newMode;
        currentMode.enter();
    }

    private void onControlChange(Fader encoder, int value) {
        currentMode.accept(encoder, value);
    }

    private void debug(String message) {
        bitwig.debug(format("%s %s", name, message));
    }

    private class FaderMode implements ObjIntConsumer<Fader> {
        private final String name;
        private final Map<Fader, Parameter> parametersByFader = new HashMap<>();

        public FaderMode(String name, List<Fader> faders, List<Parameter> parameters) {
            this.name = name;
            for(int i = 0 ; i < parameters.size(); i++) parametersByFader.put(faders.get(i), parameters.get(i));
        }

        public FaderMode(String name) {
            this(name, Collections.emptyList(), Collections.emptyList());
        }

        @Override
        public void accept(Fader fader, int value) {
            Parameter parameter = parametersByFader.get(fader);
            parameter.set((double) value / Fader.MAX_VALUE);
        }

        public void enter() {
            parametersByFader.values().forEach(p -> p.setIndication(true));
            debug(format("-> %s", this));
        }

        public void exit() {
            parametersByFader.values().forEach(p -> p.setIndication(false));
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
