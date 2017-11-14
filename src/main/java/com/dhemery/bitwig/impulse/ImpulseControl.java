package com.dhemery.bitwig.impulse;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.Transport;
import com.dhemery.bitwig.*;
import com.dhemery.impulse.Impulse;
import com.dhemery.midi.ControlChangeProcessor;

import static com.dhemery.impulse.Port.USB;

public class ImpulseControl extends ControllerExtension {
    private final Display display;
    private final ControlChangeProcessor dispatcher;

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
        Transport transport = host.createTransport();
        Impulse impulse = new Impulse(midiOutPort);

        new NoteInputController(midiInPort, USB.displayName(), impulse, dispatcher, display);
        new TransportController(transport, impulse, dispatcher);
        new MixerController(host, impulse, dispatcher);
        new PluginEncoders(host, impulse, dispatcher);

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
