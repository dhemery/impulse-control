package com.dhemery.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import java.util.Arrays;

public class Explorer {
    public static void main(String[] argv) {
        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        Arrays.stream(midiDeviceInfo)
                .map(d -> String.format(
                        "Name: '%s' Vendor: '%s' Version; '%s' Description: '%s', Class: %s",
                        d.getName(),
                        d.getVendor(),
                        d.getVersion(),
                        d.getDescription(),
                        d.getClass()))
                .forEach(System.out::println);
    }
}
