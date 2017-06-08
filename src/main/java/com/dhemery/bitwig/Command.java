package com.dhemery.bitwig;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;

import java.util.function.Consumer;

public interface Command extends Consumer<ShortMidiMessage> {
}
