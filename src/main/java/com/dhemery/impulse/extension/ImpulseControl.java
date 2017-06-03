package com.dhemery.impulse.extension;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;
import com.dhemery.bitwig.Studio;
import com.dhemery.bitwig.TranslateMidiMessageToBitwigCommand;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import static com.dhemery.impulse.extension.ImpulsePort.USB;
import static javax.sound.midi.ShortMessage.NOTE_ON;

public class ImpulseControl extends ControllerExtension {
    private TranslateMidiMessageToBitwigCommand midiToCommand;

    ImpulseControl(ImpulseControlDefinition definition, ControllerHost host) {
        super(definition, host);
    }

    @Override
    public void init() {
        ControllerHost host = getHost();
        midiToCommand = new TranslateMidiMessageToBitwigCommand();

        createNoteInputFor(USB);
        getMidiInPort(USB.ordinal()).setMidiCallback(this::executeMidiShortMessage);

        host.showPopupNotification(String.format("%s initialized", name()));
    }

    private void executeMidiShortMessage(int status, int data1, int data2) {
        midiToCommand
                .apply(shortMessage(status, data1, data2))
                .execute(getHost());
    }

    private ShortMessage shortMessage(int status, int data1, int data2) {
        try {
            return new ShortMessage(status, data1, data2);
        } catch (InvalidMidiDataException cause) {
            throw new ImpulseControlException("Cannot create MIDI short message", cause);
        }
    }

    @Override
    public void flush() {
        getHost().showPopupNotification(String.format("%s flushed", name()));
    }

    @Override
    public void exit() {
        getHost().showPopupNotification(String.format("%s exited", name()));
    }

    private void createNoteInputFor(ImpulsePort port) {
        String[] masks = Studio.noteInputMasks(NOTE_ON, ShortMessage.NOTE_OFF);
        getMidiInPort(port.ordinal()).createNoteInput(port.noteInputName(), masks);
    }

    private String name() {
        return getExtensionDefinition().getName();
    }
}
