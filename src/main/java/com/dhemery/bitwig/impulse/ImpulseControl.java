package com.dhemery.bitwig.impulse;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.*;
import com.dhemery.bitwig.*;
import com.dhemery.impulse.Control;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeProcessor;

import java.util.List;

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
    private static final int REMOTE_CONTROL_SCALE = 101;
    private static final int CHANNEL_PAN_SCALE = 201;
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
        List<Control> mixerFaders = impulse.mixerFaders();
        List<Control> mixerEncoders = impulse.mixerEncoders();

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

        TrackBank trackBank = host.createTrackBank(mixerEncoders.size(), 0, 0);
        for (int c = 0; c < trackBank.getSizeOfBank(); c++) {
            Track channel = trackBank.getChannel(c);
            Control fader = mixerFaders.get(c);
            Control encoder = mixerEncoders.get(c);
            dispatcher.register(fader, new SetParameterValue(channel.getVolume()));
            dispatcher.register(encoder, new IncrementParameterValue(channel.getPan(), CHANNEL_PAN_SCALE));
        }

        CursorRemoteControlsPage cursorRemoteControlsPage = host.createCursorTrack(0, 0).createCursorDevice().createCursorRemoteControlsPage(mixerEncoders.size());
        for (int p = 0; p < cursorRemoteControlsPage.getParameterCount(); p++) {
            dispatcher.register(mixerEncoders.get(p), new IncrementParameterValue(cursorRemoteControlsPage.getParameter(p), REMOTE_CONTROL_SCALE));
        }

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
