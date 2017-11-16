package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.ControllerHost;

public class Display {
    private final ControllerHost host;
    private final String name;

    public Display(ControllerHost host, String name) {
        this.host = host;
        this.name = name;
    }

}
