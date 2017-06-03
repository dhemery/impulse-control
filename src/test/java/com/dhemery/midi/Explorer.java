package com.dhemery.midi;

import com.dhemery.impulse.extension.ImpulsePort;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import java.util.Arrays;

public class Explorer {
    public static void main(String[] argv) {
        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        Arrays.stream(midiDeviceInfo)
                .map(d -> String.format("Name: '%s' Vendor: '%s' Version; '%s' Description: '%s'", d.getName(), d.getVendor(), d.getVersion(), d.getDescription()))
                .forEach(System.out::println);

        System.out.println("Input Port names:");
        Arrays.stream(ImpulsePort.inputPortAutoDetectionNames())
                .map(n -> String.format("'%s'", n))
                .forEach(System.out::println);

        System.out.println("Output Port names:");
        Arrays.stream(ImpulsePort.outputPortAutoDetectionNames())
                .map(n -> String.format("'%s'", n))
                .forEach(System.out::println);
    }
}
