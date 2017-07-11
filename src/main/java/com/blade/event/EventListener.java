package com.blade.event;

@FunctionalInterface
public interface EventListener {

    void trigger(Event e);

}