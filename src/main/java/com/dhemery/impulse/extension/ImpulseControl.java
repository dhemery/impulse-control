package com.dhemery.impulse.extension;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;
import com.dhemery.bitwig.TranslateMidiMessageToBitwigCommand;
import com.dhemery.bitwig.Studio;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class ImpulseControl extends ControllerExtension {

    // TODO: These names do not satisfy Studio extension auto-detect,
    // but are just fine for the note input names.
    static final String[] INPUT_PORT_NAMES = {"USB In", "MIDI In"};
    static final String[] OUTPUT_PORT_NAMES = {"USB Out"};

    private static final int USB_INPUT_PORT_NUMBER = 0;
    private TranslateMidiMessageToBitwigCommand midiToCommand;

    ImpulseControl(ImpulseControlDefinition definition, ControllerHost host) {
        super(definition, host);
    }

    @Override
    public void init() {
        ControllerHost host = getHost();
        midiToCommand = new TranslateMidiMessageToBitwigCommand();


        createNoteInputFor(USB_INPUT_PORT_NUMBER);
        getMidiInPort(USB_INPUT_PORT_NUMBER).setMidiCallback(this::executeMidiShortMessage);

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

    private void createNoteInputFor(int portNumber) {
        String[] masks = Studio.noteInputMasks(ShortMessage.NOTE_ON, ShortMessage.NOTE_OFF);
        getMidiInPort(portNumber).createNoteInput(INPUT_PORT_NAMES[portNumber], masks);
    }

    private String name() {
        return getExtensionDefinition().getName();
    }
}
