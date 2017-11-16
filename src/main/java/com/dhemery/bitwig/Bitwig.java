package com.dhemery.bitwig;

import com.bitwig.extension.controller.api.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class Bitwig {
    private final ControllerHost host;
    private final String name;
    private final List<Channel> channels;
    private final List<Parameter> remoteControls;

    public Bitwig(ControllerHost host, String name, int bankSize) {
        this.host = host;
        this.name = name;
        TrackBank trackBank = host.createTrackBank(bankSize, 0, 0);

        CursorRemoteControlsPage remoteControlBank = host.createCursorTrack(0, 0)
                .createCursorDevice()
                .createCursorRemoteControlsPage(bankSize);

        channels = IntStream.range(0, bankSize)
                .mapToObj(trackBank::getChannel)
                .collect(toList());

        remoteControls = IntStream.range(0, bankSize)
                .mapToObj(remoteControlBank::getParameter)
                .map(Parameter.class::cast)
                .collect(toList());
    }

    public List<Parameter> channelParameters(Function<Channel, Parameter> mapper) {
        return channels.stream()
                .map(mapper)
                .collect(toList());
    }

    public List<Parameter> remoteControls() {
        return remoteControls;
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
