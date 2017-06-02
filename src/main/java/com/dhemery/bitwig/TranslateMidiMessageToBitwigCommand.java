package com.dhemery.bitwig;

import com.dhemery.bitwig.commands.WriteToConsole;

import javax.sound.midi.ShortMessage;
import java.util.function.Function;

public class TranslateMidiMessageToBitwigCommand implements Function<ShortMessage, BitwigCommand> {
    @Override
    public BitwigCommand apply(ShortMessage message) {
        return new WriteToConsole(String.format("Received MIDI %X%X[%02X,%02X]", message.getCommand() >> 4, message.getChannel(), message.getData1(), message.getData2()));
    }
}
