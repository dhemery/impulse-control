package com.dhemery.impulse;

public interface Midi {
    enum Status {
        NOTE_OFF(0x8),
        NOTE_ON(0x9),
        CONTROL_CHANGE(0xB),
        CHANNEL_PRESSURE(0xD),
        PITCH_BEND(0xE);

        public final int code;

        Status(int code) {
            this.code = code;
        }
    }
}
