package com.blade.event;

@FunctionalInterface
public interface EventListener {

    void handleEvent(Event e);

}