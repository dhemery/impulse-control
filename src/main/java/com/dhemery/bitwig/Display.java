package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.ControllerHost;

public class Display {
    private final ControllerHost host;
    private final String name;

    public Display(ControllerHost host, String name) {
        this.host = host;
        this.name = name;
    }

    public void status(String status) {
        host.showPopupNotification(String.format("%s: %s", name, status));
    }

    public void debug(String message) {
        host.println(message);
    }

    public void error(String error) {
        host.errorln(error);
    }
}
