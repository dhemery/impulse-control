package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.*;
import com.dhemery.impulse.Impulse;
import com.dhemery.impulse.LinearEncoder;
import com.dhemery.impulse.MomentaryButton;
import com.dhemery.impulse.RotaryEncoder;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.List;

public class PluginEncoders {
    private static final int VOLUME_FADER_RESOLUTION = 128;
    private static final int PAN_ENCODER_RESOLUTION = 201; // Range from -100% to 100% in 1% increments


    public PluginEncoders(ControllerHost host, Impulse impulse, ControlChangeDispatcher dispatcher) {
        List<RotaryEncoder> encoders = impulse.mixerEncoders();

        CursorTrack cursorTrack = host.createCursorTrack(2, 0);
        PinnableCursorDevice cursorDevice = cursorTrack.createCursorDevice();
        CursorRemoteControlsPage cursorRemoteControlsPage = cursorDevice.createCursorRemoteControlsPage(encoders.size());

        for (int i = 0; i < encoders.size(); i++) {
            connectRemoteControlEncoder(dispatcher, cursorRemoteControlsPage.getParameter(i), encoders.get(i));
        }
    }

    private void connectRemoteControlEncoder(ControlChangeDispatcher dispatcher, RemoteControl remoteControl, RotaryEncoder encoder) {
        remoteControl.name().markInterested();
        remoteControl.markInterested();
        remoteControl.setIndication(true);
        dispatcher.register(encoder, v -> remoteControl.inc(encoder.directionOf(v), 201));
    }
}
