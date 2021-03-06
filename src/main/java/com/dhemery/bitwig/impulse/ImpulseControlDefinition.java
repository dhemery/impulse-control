package com.dhemery.bitwig.impulse;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;
import com.dhemery.impulse.Port;

import java.util.Arrays;
import java.util.UUID;

public class ImpulseControlDefinition extends ControllerExtensionDefinition {
    private static final String AUTHOR = "Dale H. Emery";
    private static final String HARDWARE_MODEL = "Impulse 49/61";
    private static final String HARDWARE_VENDOR = "Novation";
    private static final UUID ID = UUID.fromString("9CA2BAD4-A569-4B43-ACC9-08EDEB0F7594");
    private static final String NAME = "Impulse Control";
    private static final int REQUIRED_BITWIG_API_VERSION = 3;
    private static final String VERSION = "0.0.1";

    @Override
    public String getHardwareVendor() {
        return HARDWARE_VENDOR;
    }

    @Override
    public String getHardwareModel() {
        return HARDWARE_MODEL;
    }

    @Override
    public int getNumMidiInPorts() {
        return Port.inPorts().length;
    }

    @Override
    public int getNumMidiOutPorts() {
        return Port.outPorts().length;
    }

    @Override
    public void listAutoDetectionMidiPortNames(AutoDetectionMidiPortNamesList list, PlatformType platformType) {
        list.add(registeredNamesOf(Port.inPorts()), registeredNamesOf(Port.outPorts()));
    }

    @Override
    public ControllerExtension createInstance(ControllerHost host) {
        return new ImpulseControl(this, host);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getAuthor() {
        return AUTHOR;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public int getRequiredAPIVersion() {
        return REQUIRED_BITWIG_API_VERSION;
    }

    private String[] registeredNamesOf(Port[] ports) {
        return Arrays.stream(ports).map(Port::registeredName).toArray(String[]::new);
    }
}
