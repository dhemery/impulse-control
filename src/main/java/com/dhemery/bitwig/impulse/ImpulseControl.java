package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.*;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.bitwig.BooleanTogglerMode;
import com.dhemery.bitwig.ForwardToNoteInput;
import com.dhemery.bitwig.ParameterSetterMode;
import com.dhemery.impulse.*;
import com.dhemery.midi.Control;
import com.dhemery.midi.ControlChangeMessage;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.dhemery.impulse.Port.USB;

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
    private static final double REMOTE_CONTROL_STEP_SIZE = 0.01;
    private static final double PAN_STEP_SIZE = 0.005;
    private static final Function<Integer, Double> ENCODER_VALUE_TO_PAN_INCREMENT = v -> PAN_STEP_SIZE * Encoder.steps(v);
    private static final Function<Integer, Double> ENCODER_VALUE_TO_REMOTE_CONTROL_INCREMENT = v -> REMOTE_CONTROL_STEP_SIZE * Encoder.steps(v);
    private static final BiConsumer<Parameter, Double> INCREMENT_PARAMETER_VALUE = SettableRangedValue::inc;
    private static final BiConsumer<Parameter, Double> SET_PARAMETER_VALUE = SettableRangedValue::set;
    private static final Function<Integer, Double> FADER_VALUE_TO_VOLUME = sv -> (double) sv / Fader.MAX_VALUE;

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

        Mode midiMode = new UninvocableMode("MIDI", bitwig::debug);

        // Initialize channel fader controller and modes
        ChannelFaderController channelFaderController = new ChannelFaderController(impulse, dispatcher, midiMode, bitwig::debug);
        List<Parameter> volumeParameters = bitwig.channelFeatures(Channel::getVolume);

        Mode channelFaderMixerMode = new ParameterSetterMode("Channel Volume", volumeParameters, FADER_VALUE_TO_VOLUME, SET_PARAMETER_VALUE);

        Runnable channelFaderMidiModeSetter = new SingletonModeSetter(channelFaderController, midiMode);
        Runnable channelFaderMixerModeSetter = new SingletonModeSetter(channelFaderController, channelFaderMixerMode);

        dispatcher.onTouch(impulse.channelMidiModeButton(), channelFaderMidiModeSetter);
        dispatcher.onTouch(impulse.channelMixerModeButton(), channelFaderMixerModeSetter);

        // Initialize channel button controller and modes
        ChannelButtonController channelButtonController = new ChannelButtonController(impulse, dispatcher, midiMode, bitwig::debug);
        Runnable channelButtonMidiModeSetter = new SingletonModeSetter(channelButtonController, midiMode);

        List<SettableBooleanValue> channelMuteStates = bitwig.channelFeatures(Channel::getMute);
        List<SettableBooleanValue> channelSoloStates = bitwig.channelFeatures(Channel::getSolo);

        Mode channelButtonSoloMode = new BooleanTogglerMode("Channel Solo", channelSoloStates, MomentaryButton::isPressed);
        Mode channelButtonMuteMode = new BooleanTogglerMode("Channel Mute", channelMuteStates, MomentaryButton::isPressed);

        Consumer<Integer> channelButtonMixerModeSetter = new MappingModeSetter(channelButtonController, v -> Toggle.isOn(v) ? channelButtonMuteMode : channelButtonSoloMode);

        dispatcher.onTouch(impulse.channelMidiModeButton(), channelButtonMidiModeSetter);
        dispatcher.onValue(impulse.channelMixerModeButton(), channelButtonMixerModeSetter);

        // Initialize encoder controller and modes
        List<Parameter> panParameters = bitwig.channelFeatures(Channel::getPan);
        List<Parameter> remoteControls = bitwig.remoteControls();

        Mode encoderMixerMode = new ParameterSetterMode("Channel Pan", panParameters, ENCODER_VALUE_TO_PAN_INCREMENT, INCREMENT_PARAMETER_VALUE);
        Mode encoderPluginMode = new ParameterSetterMode("Remote Control", remoteControls, ENCODER_VALUE_TO_REMOTE_CONTROL_INCREMENT, INCREMENT_PARAMETER_VALUE);

        EncoderController encoderBankController = new EncoderController(impulse, dispatcher, midiMode, bitwig::debug);
        Runnable encoderMidiModeSetter = new SingletonModeSetter(encoderBankController, midiMode);
        Runnable encoderMixerModeSetter = new SingletonModeSetter(encoderBankController, encoderMixerMode);
        Runnable encoderPluginModeSetter = new SingletonModeSetter(encoderBankController, encoderPluginMode);

        dispatcher.onTouch(impulse.encoderMidiModeButton(), encoderMidiModeSetter);
        dispatcher.onTouch(impulse.encoderMixerModeButton(), encoderMixerModeSetter);
        dispatcher.onTouch(impulse.encoderPluginModeButton(), encoderPluginModeSetter);

        Stream.of(impulse.channelMidiModeButton(), impulse.encoderMidiModeButton())
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
