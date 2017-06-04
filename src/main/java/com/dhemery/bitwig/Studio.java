package com.dhemery.bitwig;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;
import com.dhemery.impulse.Port;
import com.dhemery.midi.MidiMessenger;

import javax.sound.midi.ShortMessage;
import java.util.Arrays;
import java.util.function.Consumer;

import static com.dhemery.impulse.Port.USB;
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
}
