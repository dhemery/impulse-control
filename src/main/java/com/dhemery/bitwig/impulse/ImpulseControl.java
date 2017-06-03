package com.dhemery.bitwig.impulse;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;
import com.dhemery.bitwig.Studio;
import com.dhemery.impulse.Controls;
import com.dhemery.midi.MidiMessenger;

import static com.dhemery.impulse.Port.USB;

public class ImpulseControl extends ControllerExtension {
    private final MidiMessenger midiMessenger;
    private final Studio studio;

    ImpulseControl(ImpulseControlDefinition definition, ControllerHost host) {
        super(definition, host);
        studio = new Studio(this);
        midiMessenger = new MidiMessenger(new Controls(studio));
    }

    @Override
    public void init() {
        studio.createNoteInputsFor(USB);

        getMidiInPort(USB.ordinal()).setMidiCallback(midiMessenger::deliver);

        studio.display("initialized");
    }

    @Override
    public void flush() {
    }

    @Override
    public void exit() {
        studio.display("exited");
    }
}
