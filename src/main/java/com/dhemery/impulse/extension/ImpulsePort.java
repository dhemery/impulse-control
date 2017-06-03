package com.dhemery.impulse.extension;

import java.util.stream.Stream;

public enum ImpulsePort {
    USB("Impulse", "Impulse USB"),
    DIN("Impulse MIDI In", "Impulse DIN");

    private final String shortName;
    private final String noteInputName;

    ImpulsePort(String name, String noteInputName) {
        this.shortName = name;
        this.noteInputName = noteInputName;
    }

    public String shortName() {
        return shortName;
    }

    public String noteInputName() {
        return noteInputName;
    }

    public String autoDetectionName() {
        // Note tne extra space before and after the short name
        // This works for MacOS. Not sure about other platforms.
        return String.format("Impulse  %s ", shortName);
    }

    public static ImpulsePort[] inputPorts() {
        return values();
    }

    public static ImpulsePort[] outputPorts() {
        return new ImpulsePort[]{USB};
    }

    public static String[] inputPortAutoDetectionNames() {
        return autoDetectionNamesFor(inputPorts());
    }

    public static String[] outputPortAutoDetectionNames() {
        return autoDetectionNamesFor(outputPorts());
    }

    private static String[] autoDetectionNamesFor(ImpulsePort[] ports) {
        return Stream.of(ports)
                .map(ImpulsePort::autoDetectionName)
                .toArray(String[]::new);
    }
}
