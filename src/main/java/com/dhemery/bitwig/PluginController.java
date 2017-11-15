package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.*;
import com.dhemery.impulse.StepperEncoder;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;

public class PluginController {
    private static final int REMOTE_CONTROL_ENCODER_RESOLUTION = 101;

    public PluginController(CursorRemoteControlsPage remoteControls, List<StepperEncoder> encoders, ControlChangeDispatcher dispatcher) {
        for (int i = 0; i < encoders.size(); i++) {
            connectRemoteControlEncoder(dispatcher, remoteControls.getParameter(i), encoders.get(i));
        }
    }

    private void connectRemoteControlEncoder(ControlChangeDispatcher dispatcher, RemoteControl remoteControl, StepperEncoder encoder) {
        remoteControl.name().markInterested();
        remoteControl.markInterested();
        remoteControl.setIndication(true);
        dispatcher.register(encoder, v -> remoteControl.inc(encoder.directionOf(v), REMOTE_CONTROL_ENCODER_RESOLUTION));
    }
}
