package com.dhemery.bitwig.impulse;

import java.util.List;
import java.util.function.Consumer;

public class ServiceMode<T extends Consumer<Integer> & Service> extends Mode<T> {

    public ServiceMode(String name, List<T> actors) {
        super(name, actors);
    }

    @Override
    public void enter() {
        eachActor(Service::activate);
    }

    @Override
    public void exit() {
        eachActor(Service::deactivate);
    }

}
