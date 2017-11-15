package com.dhemery.bitwig.impulse;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.*;
import com.dhemery.bitwig.*;
import com.dhemery.impulse.Control;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.StepperEncoder;
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
        List<StepperEncoder> mixerEncoders = impulse.mixerEncoders();
        List<Control> mixerFaders = impulse.mixerFaders();

        NoteInput noteInput = midiInPort.createNoteInput(USB.displayName(), NOTE_INPUT_MESSAGE_MASKS);
        new NoteInputController(noteInput, impulse, dispatcher);

        Transport transport = host.createTransport();
        new TransportController(transport, impulse, dispatcher);

        TrackBank trackBank = host.createTrackBank(mixerEncoders.size(), 0, 0);
        new MixerController(trackBank, mixerFaders, mixerEncoders, dispatcher);

        CursorRemoteControlsPage cursorRemoteControlsPage = host.createCursorTrack(0, 0).createCursorDevice().createCursorRemoteControlsPage(mixerEncoders.size());
        new PluginController(cursorRemoteControlsPage, impulse.mixerEncoders(), dispatcher);

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
