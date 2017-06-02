package com.dhemery.impulse;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.NoteInput;
import sun.util.resources.cldr.mas.CalendarData_mas_KE;

public class ImpulseControl extends ControllerExtension {

    // TODO: These names do not satisfy Bitwig controller auto-detect,
    // but are just fine for the note input names.
    static final String[] INPUT_PORT_NAMES = {"USB In", "MIDI In"};
    static final String[] OUTPUT_PORT_NAMES = {"USB Out"};

    private static final int USB_INPUT_PORT_NUMBER = 0;
    public static final String STATUS_CODE_MASK_FORMAT = "%x?????";

    ImpulseControl(ImpulseControlDefinition definition, ControllerHost host) {
        super(definition, host);
    }

    @Override
    public void init() {
        ControllerHost host = getHost();

        createNoteInputFor(USB_INPUT_PORT_NUMBER);
        getMidiInPort(USB_INPUT_PORT_NUMBER).setMidiCallback(this::receiveMidi);

        host.showPopupNotification(String.format("%s initialized", name()));
    }

    private void receiveMidi(int status, int data1, int data2) {
        getHost().println(String.format("received MIDI message %x, %x, %x", status, data1, data2));
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
        getMidiInPort(portNumber).createNoteInput(INPUT_PORT_NAMES[portNumber],
                mask(Midi.Status.NOTE_ON),
                mask(Midi.Status.NOTE_OFF));
    }

    private String mask(Midi.Status status) {
        return String.format(STATUS_CODE_MASK_FORMAT, status.code);
    }

    private String name() {
        return getExtensionDefinition().getName();
    }
}
