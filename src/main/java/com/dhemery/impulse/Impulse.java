package com.dhemery.impulse;

import com.bitwig.extension.controller.api.MidiOut;

public class Impulse {
    private static final String SYSEX_MESSAGE_START = "F0";
    private static final String NOVATION_ID = "00 20 29";
    private static final String SENDER_ID = "67";
    private static final String SYSEX_MESSAGE_END = "F7";
    private static final String MESSAGE_FORMAT = String.join(" ",
            SYSEX_MESSAGE_START,
            NOVATION_ID,
            SENDER_ID,
            "%s",
            SYSEX_MESSAGE_END);
    private static final String CONNECT_TO_COMPUTER = sysexMessage("06 01 01 01");

    public Impulse(MidiOut port) {
        port.sendSysex(CONNECT_TO_COMPUTER);
    }

    private static String sysexMessage(String content) {
        return String.format(MESSAGE_FORMAT, content);
    }
}
