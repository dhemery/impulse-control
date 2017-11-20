package com.dhemery.midi;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ControlChangeDispatcher {
    void onMessage(Control control, Consumer<ControlChangeMessage> message);

    void onValue(Control control, Consumer<Integer> action);

    <T extends Control> void onValue(T control, BiConsumer<? super T, Integer> action);

    void onTouch(Control control, Runnable action);
}
