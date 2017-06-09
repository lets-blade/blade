package com.blade.event;

import com.blade.Blade;

public class Event {

    public EventType eventEventType;
    public Blade blade;

    public Event(EventType eventEventType) {
        this.eventEventType = eventEventType;
    }

    public Event(EventType eventEventType, Blade blade) {
        this.eventEventType = eventEventType;
        this.blade = blade;
    }

}