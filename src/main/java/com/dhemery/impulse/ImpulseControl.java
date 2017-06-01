package com.dhemery.impulse;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;

public class ImpulseControl extends ControllerExtension {
    // TODO: These names do not satisfy Bitwig controller auto-detect,
    // but are just fine for the note input names.
    static final String[] INPUT_PORT_NAMES = {"USB In", "MIDI In"};
    static final String[] OUTPUT_PORT_NAMES = {"USB Out"};

    private static final int USB_INPUT_PORT_NUMBER = 0;
    private final ImpulseControlDefinition definition;
    private final ControllerHost host;
    private boolean debug;

    ImpulseControl(ImpulseControlDefinition definition, ControllerHost host) {
        super(definition, host);
        this.definition = definition;
        this.host = host;
    }

    @Override
    public void init() {
        new BooleanPreference(host, "debug", getClass().getName(), b -> debug = b);

        createNoteInputFor(USB_INPUT_PORT_NUMBER);

        host.showPopupNotification(String.format("%s initialized", definition.getName()));
    }

    private void createNoteInputFor(int portNumber) {
        host.getMidiInPort(portNumber).createNoteInput(INPUT_PORT_NAMES[portNumber]);
    }

    @Override
    public void flush() {
    }

    @Override
    public void exit() {
    }
}
