package com.blade.event;

import com.blade.Blade;

public class Event {

    public EventType eventType;
    public Blade     blade;

    public Event(EventType eventType) {
        this.eventType = eventType;
    }

    public Event(EventType eventType, Blade blade) {
        this.eventType = eventType;
        this.blade = blade;
    }

}