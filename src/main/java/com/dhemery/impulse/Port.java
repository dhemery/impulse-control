package com.dhemery.impulse;

public enum Port {
    USB("Impulse  Impulse ", "Impulse USB"),
    DIN("Impulse  Impulse MIDI In ", "Impulse DIN");

    private static final Port[] outPorts = { USB };

    private final String registeredName;
    private final String displayName;

    Port(String registeredName, String displayName) {
        this.registeredName = registeredName;
        this.displayName = displayName;
    }

    public String registeredName() {
        return registeredName;
    }

    public String displayName() {
        return displayName;
    }

    public static Port[] inPorts() {
        return values();
    }

    public static Port[] outPorts() { return outPorts; }
}
