package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.*;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.bitwig.BooleanTogglerMode;
import com.dhemery.bitwig.ForwardToNoteInput;
import com.dhemery.impulse.Encoder;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.MomentaryButton;
import com.dhemery.impulse.Toggle;
import com.dhemery.midi.Control;
import com.dhemery.midi.ControlChangeMessage;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.dhemery.impulse.Port.USB;
import static java.lang.String.format;

public class ImpulseControl extends ControllerExtension {
    private static final String[] NOTE_INPUT_MESSAGE_MASKS = {
            "8?????",   // Note Off, any channel.
            "9?????",   // Note On, any channel.
            // A?????      Impulse does not send Key Aftertouch
            "B201??",   // Mod Wheel. The extension handles all other CC messages.
            "C?????",   // Program Change (any channel.
            "D?????",   // Channel Aftertouch, any channel.
            "E?????"    // Pitch Bend, any channel.
    };
    private static final double PAN_PARAMETER_INCREMENT = 0.01;
    private static final double REMOTE_CONTROL_PARAMETER_INCREMENT = 0.01;
    private static final double VOLUME_PARAMETER_RANGE = 1.0;

    private final ControlChangeProcessor dispatcher;
    private final ImpulseControlDefinition definition;
    private Bitwig bitwig;

    ImpulseControl(ImpulseControlDefinition definition, ControllerHost host) {
        super(definition, host);
        this.definition = definition;
        dispatcher = new ControlChangeProcessor(this::warnUnhandled);
    }

    @Override
    public void init() {
        ControllerHost host = getHost();
        host.shouldFailOnDeprecatedUse();
        MidiIn midiInPort = host.getMidiInPort(USB.ordinal());
        MidiOut midiOutPort = host.getMidiOutPort(USB.ordinal());

        Impulse impulse = new Impulse(midiOutPort);
        List<Control> midiControls = impulse.midiControls();
        List<Encoder> mixerEncoders = impulse.mixerEncoders();

        int bankSize = mixerEncoders.size();
        bitwig = new Bitwig(host, definition.getName(), bankSize);

        NoteInput noteInput = midiInPort.createNoteInput(USB.displayName(), NOTE_INPUT_MESSAGE_MASKS);
        midiControls.forEach(c -> dispatcher.onMessage(c, new ForwardToNoteInput(noteInput)));

        Transport transport = host.createTransport();
        SettableBooleanValue loopEnabled = transport.isArrangerLoopEnabled();
        loopEnabled.markInterested();
        dispatcher.onValue(impulse.playButton(), new RunOnButtonPress(transport::play));
        dispatcher.onValue(impulse.stopButton(), new RunOnButtonPress(transport::stop));
        dispatcher.onValue(impulse.rewindButton(), new RunOnButtonPress(transport::rewind));
        dispatcher.onValue(impulse.fastForwardButton(), new RunOnButtonPress(transport::fastForward));
        dispatcher.onValue(impulse.loopButton(), new RunOnButtonPress(loopEnabled::toggle));
        dispatcher.onValue(impulse.recordButton(), new RunOnButtonPress(transport::record));


        new EncoderBankController(impulse, bitwig, dispatcher);
        new FaderBankController(impulse, bitwig, dispatcher);

        Mode midiMode = new UninvocableMode("MIDI", bitwig::debug);
        ButtonBankController buttonBankController = new ButtonBankController(impulse, bitwig, dispatcher, midiMode);
        Runnable midiModeSetter = new SingletonModeSetter(buttonBankController, midiMode);

        List<SettableBooleanValue> muteStates = bitwig.channelFeatures(Channel::getMute);
        List<SettableBooleanValue> soloStates = bitwig.channelFeatures(Channel::getSolo);

        Mode channelSoloMode = new BooleanTogglerMode("Channel Solo", soloStates, MomentaryButton::isPressed);
        Mode channelMuteMode = new BooleanTogglerMode("Channel Mute", muteStates, MomentaryButton::isPressed);

        Consumer<Integer> mixerModeSetter = new MappingModeSetter(buttonBankController, v -> Toggle.isOn(v) ? channelMuteMode : channelSoloMode);

        dispatcher.onTouch(impulse.faderMidiModeButton(), midiModeSetter);
        dispatcher.onValue(impulse.faderMixerModeButton(), mixerModeSetter);

        Stream.of(impulse.faderMidiModeButton(), impulse.encoderMidiModeButton())
                .forEach(impulse::select);

        midiInPort.setMidiCallback(dispatcher);

        bitwig.status("initialized");
    }

    @Override
    public void exit() {
        bitwig.status("exited");
    }

    @Override
    public void flush() {
    }

    private void warnUnhandled(ControlChangeMessage message) {
        String warning = String.format("Unhandled MIDI CC %s value %02X", message.identifier(), message.value());
        bitwig.debug(warning);
    }
}
