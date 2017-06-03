package com.dhemery.bitwig;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;
import com.dhemery.impulse.Port;

import java.util.Arrays;

import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

public class Studio {
    private static String STATUS_CODE_MASK_FORMAT = "%x?????";
    private final ControllerHost host;
    private final ControllerExtension controller;
    private final ControllerExtensionDefinition definition;

    public Studio(ControllerExtension controller) {
        this.controller = controller;
        this.host = controller.getHost();
        this.definition = controller.getExtensionDefinition();
    }

    public void createNoteInputsFor(Port... ports) {
        String[] masks = noteInputMasks(NOTE_ON, NOTE_OFF);
        Arrays.stream(ports)
                .forEach(p -> controller.getMidiInPort(p.ordinal()).createNoteInput(p.displayName(), masks));
    }

    private static String[] noteInputMasks(int... statusBytes) {
        return Arrays.stream(statusBytes)
                .map(statusByte -> statusByte >>> 4)
                .mapToObj(statusNibble -> String.format(STATUS_CODE_MASK_FORMAT, statusNibble))
                .toArray(String[]::new);
    }

    public void display(String format, Object... details) {
        String message = String.format(format, details);
        host.showPopupNotification(String.format("%s: %s", definition.getName(), message));
    }

    public void debug(String format, Object... details) {
        host.println(String.format(format, details));
    }
}
