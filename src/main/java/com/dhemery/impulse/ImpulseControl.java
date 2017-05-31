package com.dhemery.impulse;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class ImpulseControl extends ControllerExtension {
    private final ControllerHost host;

    public ImpulseControl(ControllerExtensionDefinition definition, ControllerHost host) {
        super(definition, host);
        this.host = host;
        host.println("ImpulseControl()");
    }

    @Override
    public void init() {
        host.println("init()");
    }

    @Override
    public void flush() {
        host.println("flush()");
    }

    @Override
    public void exit() {
        host.println("exit()");
    }
}
