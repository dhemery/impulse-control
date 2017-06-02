package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.ControllerHost;

public interface BitwigCommand {
    void execute(ControllerHost host);
}
