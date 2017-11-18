package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.Value;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.MomentaryButton;
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
        List<MomentaryButton> buttons = impulse.mixerButtons();

        soloMode = new ButtonMode("Channel Solo Button", buttons, soloStates);
        muteMode = new ButtonMode("Channel Mute Button", buttons, muteStates);

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
        this.mode = mode;
        this.mode.enter();
        bitwig.status(String.format("%s mode", this.mode));
    }

    private void onMuteSoloButtonStateChange(MomentaryButton button, int state) {
        mode.accept(button, state);
    }

    private class ButtonMode implements ObjIntConsumer<MomentaryButton> {
        private final String name;
        private final HashMap<MomentaryButton, SettableBooleanValue> channelStateByButton = new HashMap<>();

        public ButtonMode(String name, List<MomentaryButton> buttons, List<SettableBooleanValue> channelStates) {
            this.name = name;
            for(int i = 0 ; i < channelStates.size(); i++) channelStateByButton.put(buttons.get(i), channelStates.get(i));
            channelStates.forEach(Value::markInterested);
        }

        public ButtonMode(String name) {
            this(name, Collections.emptyList(), Collections.emptyList());
        }

        @Override
        public void accept(MomentaryButton button, int buttonState) {
            SettableBooleanValue channelState = channelStateByButton.get(button);
            if(button.isPressed(buttonState)) channelState.toggle();
            setIllumination(button, channelState);
        }

        private void setIllumination(MomentaryButton button, SettableBooleanValue channelState) {
            impulse.setLight(button, channelState.get());
        }

        public void enter() {
            channelStateByButton.forEach(this::setIllumination);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}




