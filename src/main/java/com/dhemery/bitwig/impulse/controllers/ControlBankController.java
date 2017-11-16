package com.dhemery.bitwig.impulse.controllers;

import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.dhemery.bitwig.Bitwig;
import com.dhemery.impulse.controls.Control;
import com.dhemery.impulse.controls.Encoder;
import com.dhemery.impulse.controls.Normalizing;
import com.dhemery.midi.ControlChangeDispatcher;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class ControlBankController<T extends Control & Normalizing> {
    private final ControlMode<T> MIDI_MODE = new ControlMode<>("MIDI", Collections.emptyList(), Collections.emptyList(), (c, v) -> {}, 1);
    private final String name;
    private final Bitwig bitwig;
    private final ControlChangeDispatcher dispatcher;
    private final List<T> controls;
    private final BiConsumer<Parameter, Double> setter;
    private ControlMode<T> currentMode;

    public ControlBankController(String name, Bitwig bitwig, ControlChangeDispatcher dispatcher, Control midiModeSelectorButton, List<T> controls, BiConsumer<Parameter, Double> setter) {
        this.name = name;
        this.bitwig = bitwig;
        this.dispatcher = dispatcher;
        this.controls = controls;
        this.setter = setter;
        controls.forEach(c -> dispatcher.onValue(c, this::forwardToMode));
        dispatcher.onTouch(midiModeSelectorButton, () -> enter(MIDI_MODE));
        currentMode = MIDI_MODE;
    }

    protected void addMode(String name, Control selectorButton, List<Parameter> parameters, double scale) {
        listenForMode(selectorButton, new ControlMode<>(name, controls, parameters, setter, scale));
    }

    private void enter(ControlMode<T> mode) {
        currentMode.exit();
        currentMode = mode;
        currentMode.enter();
        bitwig.status(String.format("Entering %s %s mode", name, currentMode));
    }

    private void forwardToMode(T control, int value) {
        currentMode.accept(control, value);
    }

    private void listenForMode(Control selectorButton, ControlMode<T> mode) {
        dispatcher.onTouch(selectorButton, () -> enter(mode));
    }
}
