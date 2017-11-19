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

public class ButtonBankController {
    private final Impulse impulse;
    private final Bitwig bitwig;
    private ButtonMode mode;
    private final ButtonMode soloMode;
    private final ButtonMode muteMode;

    public ButtonBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        this.impulse = impulse;
        this.bitwig = bitwig;
        List<SettableBooleanValue> muteStates = bitwig.channelFeatures(Channel::getMute);
        List<SettableBooleanValue> soloStates = bitwig.channelFeatures(Channel::getSolo);
        List<IlluminableMomentaryButton> buttons = impulse.mixerButtons();

        soloMode = new ButtonMode("Channel Solo", buttons, soloStates);
        muteMode = new ButtonMode("Channel Mute", buttons, muteStates);

        ButtonMode midiMode = new ButtonMode("MIDI");
        mode = midiMode;

        dispatcher.onTouch(impulse.faderMidiModeButton(), () -> enter(midiMode));
        dispatcher.onValue(impulse.faderMixerModeButton(), this::enterMixerButtonMode);

        buttons.forEach(c -> dispatcher.onValue(c, this::onMuteSoloButtonStateChange));
    }

    private void enterMixerButtonMode(Toggle button, int buttonState) {
        enter(button.isOn(buttonState) ? muteMode : soloMode);
    }

    private void enter(ButtonMode mode) {
        if (this.mode == mode) return;
        mode.exit();
        this.mode = mode;
        mode.enter();
        bitwig.debug(String.format("Buttons -> %s", this.mode));
    }

    private void onMuteSoloButtonStateChange(IlluminableMomentaryButton button, int state) {
        mode.accept(button, state);
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

        @Override
        public String toString() {
            return name;
        }

        public void enter() {
            parametersByButton.values().forEach(Subscribable::subscribe);
            parametersByButton.forEach((b, v) -> impulse.illuminate(b, v.get()));
        }

        public void exit() {
            parametersByButton.values().forEach(Subscribable::unsubscribe);
        }
    }
}
