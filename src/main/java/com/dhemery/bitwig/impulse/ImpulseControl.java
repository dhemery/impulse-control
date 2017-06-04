package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Transport;
import com.dhemery.bitwig.Display;
import com.dhemery.bitwig.Studio;
import com.dhemery.impulse.Control;
import com.dhemery.impulse.Controls;
import com.dhemery.midi.MidiMessenger;

import javax.sound.midi.ShortMessage;

import static com.dhemery.impulse.Port.USB;
import static javax.sound.midi.ShortMessage.CONTROL_CHANGE;

public class ImpulseControl extends ControllerExtension {
    private final MidiMessenger midiMessenger;
    private final Studio studio;
    private final Display display;
    private final Controls controls;

    ImpulseControl(ImpulseControlDefinition definition, ControllerHost host) {
        super(definition, host);
        display = new Display(host, definition.getName());
        studio = new Studio(this);
        controls = new Controls(this::warnUnhandled);
        midiMessenger = new MidiMessenger(controls);
    }

    @Override
    public void init() {
        studio.createNoteInputsFor(USB);

        getMidiInPort(USB.ordinal()).setMidiCallback(midiMessenger::deliver);

        Transport transport = getHost().createTransport();

        controls.install(new Control(CONTROL_CHANGE, 0x1B, m -> { if(m.getData2() > 0 ) transport.rewind(); }));
        controls.install(new Control(CONTROL_CHANGE, 0x1C, m -> { if(m.getData2() > 0 ) transport.fastForward(); }));
        controls.install(new Control(CONTROL_CHANGE, 0x1D, m -> { if(m.getData2() > 0 ) transport.stop(); }));
        controls.install(new Control(CONTROL_CHANGE, 0x1E, m -> { if(m.getData2() > 0 ) transport.play(); }));

        display.status("initialized");
    }

    @Override
    public void flush() {
    }

    @Override
    public void exit() {
        display.status("exited");
    }

    private void warnUnhandled(ShortMessage message) {
        String warning = String.format("Unhandled MIDI %X%X[%02X,%02X]", message.getCommand() >> 4, message.getChannel(), message.getData1(), message.getData2());
        display.debug(warning);
    }
}
