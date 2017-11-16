package com.dhemery.bitwig.impulse;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.*;
import com.dhemery.bitwig.*;
import com.dhemery.bitwig.commands.ActIfButtonPressed;
import com.dhemery.bitwig.commands.ForwardToNoteInput;
import com.dhemery.bitwig.impulse.controllers.EncoderBankController;
import com.dhemery.bitwig.impulse.controllers.FaderBankController;
import com.dhemery.impulse.controls.Control;
import com.dhemery.impulse.controls.Encoder;
import com.dhemery.impulse.controls.Fader;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeProcessor;

import java.util.List;
import java.util.stream.IntStream;

import static com.dhemery.impulse.Port.USB;
import static java.util.stream.Collectors.toList;

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
    private final Display display;

    ImpulseControl(ImpulseControlDefinition definition, ControllerHost host) {
        super(definition, host);
        display = new Display(host, definition.getName());
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
        List<Fader> mixerFaders = impulse.mixerFaders();
        List<Encoder> mixerEncoders = impulse.mixerEncoders();

        int bankSize = mixerEncoders.size();
        TrackBank trackBank = host.createTrackBank(bankSize, 0, 0);
        CursorRemoteControlsPage remoteControlBank = host.createCursorTrack(0, 0)
                .createCursorDevice()
                .createCursorRemoteControlsPage(bankSize);

        NoteInput noteInput = midiInPort.createNoteInput(USB.displayName(), NOTE_INPUT_MESSAGE_MASKS);
        midiControls.forEach(c -> dispatcher.register(c, new ForwardToNoteInput(noteInput, c.identifier.cc)));

        Transport transport = host.createTransport();
        SettableBooleanValue loopEnabled = transport.isArrangerLoopEnabled();
        loopEnabled.markInterested();
        dispatcher.register(impulse.playButton(), new ActIfButtonPressed(transport::play));
        dispatcher.register(impulse.stopButton(), new ActIfButtonPressed(transport::stop));
        dispatcher.register(impulse.rewindButton(), new ActIfButtonPressed(transport::rewind));
        dispatcher.register(impulse.fastForwardButton(), new ActIfButtonPressed(transport::fastForward));
        dispatcher.register(impulse.loopButton(), new ActIfButtonPressed(loopEnabled::toggle));
        dispatcher.register(impulse.recordButton(), new ActIfButtonPressed(transport::record));

        List<Track> tracks = IntStream.range(0, trackBank.getSizeOfBank())
                .mapToObj(trackBank::getChannel)
                .collect(toList());
        List<Parameter> panParameters = tracks.stream().map(Channel::getPan).collect(toList());
        List<Parameter> volumeParameters = tracks.stream().map(Channel::getVolume).collect(toList());

        List<Parameter> remoteControlParameters = IntStream.range(0, trackBank.getSizeOfBank())
                .mapToObj(remoteControlBank::getParameter)
                .collect(toList());

        new EncoderBankController(impulse, panParameters, remoteControlParameters, dispatcher, display);
        new FaderBankController(impulse, volumeParameters, dispatcher, display);

        midiInPort.setMidiCallback(dispatcher);

        display.status("initialized");
    }

    @Override
    public void exit() {
        display.status("exited");
    }

    @Override
    public void flush() {
    }

    private void warnUnhandled(ShortMidiMessage message) {
        String warning = String.format("Unhandled MIDI %X%X[%02X,%02X]", message.getStatusByte() >> 4, message.getChannel(), message.getData1(), message.getData2());
        display.debug(warning);
    }
}
