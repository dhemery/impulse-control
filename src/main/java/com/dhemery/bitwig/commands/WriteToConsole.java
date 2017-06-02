package com.dhemery.bitwig.commands;

import com.bitwig.extension.controller.api.ControllerHost;
import com.dhemery.bitwig.BitwigCommand;

public class WriteToConsole implements BitwigCommand {
    private final String message;

    public WriteToConsole(String message) {
        this.message = message;
    }

    @Override
    public void execute(ControllerHost host) {
        host.println(message);
    }
}
