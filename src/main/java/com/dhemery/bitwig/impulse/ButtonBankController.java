package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.bitwig.BooleanTogglerMode;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.MomentaryButton;
import com.dhemery.impulse.Toggle;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class ButtonBankController {
    private final Bitwig bitwig;
    private Mode currentMode;
    private final Mode soloMode;
    private final Mode muteMode;
    private final String name;

    public ButtonBankController(Impulse impulse, Bitwig bitwig, ControlChangeDispatcher dispatcher) {
        name = "Buttons";
        this.bitwig = bitwig;
        List<? extends MomentaryButton> buttons = impulse.mixerButtons();
        List<SettableBooleanValue> muteStates = bitwig.channelFeatures(Channel::getMute);
        List<SettableBooleanValue> soloStates = bitwig.channelFeatures(Channel::getSolo);

        soloMode = new BooleanTogglerMode("Channel Solo", soloStates, MomentaryButton::isPressed);
        muteMode = new BooleanTogglerMode("Channel Mute", muteStates, MomentaryButton::isPressed);

        Mode midiMode = new Mode("MIDI");
        currentMode = midiMode;

        dispatcher.onTouch(impulse.faderMidiModeButton(), () -> enter(midiMode));
        dispatcher.onValue(impulse.faderMixerModeButton(), this::onMixerModeButtonChange);

        IntStream.range(0, buttons.size())
                .forEach(i -> dispatcher.onValue(buttons.get(i), v -> currentMode.accept(i, v)));
    }

    private void onMixerModeButtonChange(Toggle button, int buttonState) {
        enter(button.isOn(buttonState) ? muteMode : soloMode);
    }

    private void enter(Mode newMode) {
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
