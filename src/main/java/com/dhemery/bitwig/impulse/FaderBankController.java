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

public class FaderBankController {
    private final Bitwig bitwig;
    private FaderMode mode;

    public FaderBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        this.bitwig = bitwig;
        List<Parameter> panParameters = bitwig.channelParameters(Channel::getVolume);
        List<Fader> faders = impulse.mixerFaders();

        FaderMode midiMode = new FaderMode("MIDI");
        FaderMode mixerMode = new FaderMode("Mixer", faders, panParameters);
        mode = midiMode;

        dispatcher.onTouch(impulse.faderMidiModeButton(), () -> enter(midiMode));
        dispatcher.onTouch(impulse.faderMixerModeButton(), () -> enter(mixerMode));

        faders.forEach(c -> dispatcher.onValue(c, this::onFaderChange));
    }

    private void enter(FaderMode newMode) {
        mode.exit();
        mode = newMode;
        mode.enter();
        bitwig.status(String.format("Fader %s mode", mode));
    }

    private void onFaderChange(Fader encoder, int value) {
        mode.accept(encoder, value);
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





