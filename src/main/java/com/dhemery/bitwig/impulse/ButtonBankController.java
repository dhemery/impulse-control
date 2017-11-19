package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.Subscribable;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.IlluminableMomentaryButton;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.Toggle;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.ObjIntConsumer;

import static java.lang.String.format;

public class ButtonBankController {
    private final Impulse impulse;
    private final Bitwig bitwig;
    private ButtonMode currentMode;
    private final ButtonMode soloMode;
    private final ButtonMode muteMode;
    private final String name;

    public ButtonBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        name = "Buttons";
        this.impulse = impulse;
        this.bitwig = bitwig;
        List<SettableBooleanValue> muteStates = bitwig.channelFeatures(Channel::getMute);
        List<SettableBooleanValue> soloStates = bitwig.channelFeatures(Channel::getSolo);
        List<IlluminableMomentaryButton> buttons = impulse.mixerButtons();

        soloMode = new ButtonMode("Channel Solo", buttons, soloStates);
        muteMode = new ButtonMode("Channel Mute", buttons, muteStates);

        ButtonMode midiMode = new ButtonMode("MIDI");
        currentMode = midiMode;

        dispatcher.onTouch(impulse.faderMidiModeButton(), () -> enter(midiMode));
        dispatcher.onValue(impulse.faderMixerModeButton(), this::onMixerModeButtonChange);

        buttons.forEach(c -> dispatcher.onValue(c, this::onControlChange));
    }

    private void onMixerModeButtonChange(Toggle button, int buttonState) {
        enter(button.isOn(buttonState) ? muteMode : soloMode);
    }

    private void enter(ButtonMode newMode) {
        if (currentMode == newMode) return;
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

    private void onControlChange(IlluminableMomentaryButton button, int state) {
        currentMode.accept(button, state);
    }

    private class ButtonMode implements ObjIntConsumer<IlluminableMomentaryButton> {
        private final String name;
        private final HashMap<IlluminableMomentaryButton, SettableBooleanValue> parametersByButton = new HashMap<>();

        public ButtonMode(String name, List<IlluminableMomentaryButton> buttons, List<SettableBooleanValue> parameters) {
            this.name = name;
            for(int i = 0 ; i < parameters.size(); i++) parametersByButton.put(buttons.get(i), parameters.get(i));
            parametersByButton.forEach((b, p) -> p.addValueObserver(v -> impulse.illuminate(b, v)));
            parameters.forEach(Subscribable::unsubscribe);
        }

        public ButtonMode(String name) {
            this(name, Collections.emptyList(), Collections.emptyList());
        }

        @Override
        public void accept(IlluminableMomentaryButton button, int value) {
            SettableBooleanValue parameter = parametersByButton.get(button);
            button.ifPressed(value, parameter::toggle);
        }

        public void enter() {
            parametersByButton.values().forEach(Subscribable::subscribe);
            parametersByButton.forEach((b, v) -> impulse.illuminate(b, v.get()));
            debug(format("-> %s", this));
        }

        public void exit() {
            parametersByButton.values().forEach(Subscribable::unsubscribe);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
