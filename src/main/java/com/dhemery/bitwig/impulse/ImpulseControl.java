package com.dhemery.bitwig.impulse;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiIn;
import com.dhemery.bitwig.Display;
import com.dhemery.bitwig.NoteInputController;
import com.dhemery.bitwig.TransportController;
import com.dhemery.midi.ControlChangeDispatcher;

import static com.dhemery.impulse.Port.USB;

public class ImpulseControl extends ControllerExtension {
    private final Display display;
    private final ControlChangeDispatcher dispatcher;

    ImpulseControl(ImpulseControlDefinition definition, ControllerHost host) {
        super(definition, host);
        display = new Display(host, definition.getName());
        dispatcher = new ControlChangeDispatcher(this::warnUnhandled);
    }

    @Override
    public void init() {
        MidiIn midiInPort = getMidiInPort(USB.ordinal());

        new NoteInputController(midiInPort, USB.displayName());
        new TransportController(getHost().createTransport(), dispatcher);

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
