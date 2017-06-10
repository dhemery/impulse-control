package com.dhemery.impulse;

public class RelativePosition extends Control {
    private final int valueOnDecrementing;
    private final int valueOnIncrementing;

    public RelativePosition(String name, int channel, int cc, int valueOnDecrementing, int valueOnIncrementing) {
        super(name, channel, cc);
        this.valueOnDecrementing = valueOnDecrementing;
        this.valueOnIncrementing = valueOnIncrementing;
    }

    public int valueOnDecrementing() {
        return valueOnDecrementing;
    }

    public int valueOnIncrementing() {
        return valueOnIncrementing;
    }
}
