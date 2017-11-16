package com.dhemery.midi;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.dhemery.bitwig.commands.ForwardToNoteInput;
import com.dhemery.impulse.controls.Control;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ControlChangeDispatcher {
    void onMessage(Control control, Consumer<ShortMidiMessage> message);
    void onValue(Control control, IntConsumer action);
    <T extends Control> void onValue(T control, BiConsumer<? super T, Integer> action);
    void onTouch(Control control, Runnable action);
}
