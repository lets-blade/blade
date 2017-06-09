package com.blade.event;

import com.blade.Blade;
import com.blade.ioc.OrderComparator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventManager {

    private Map<EventType, List<EventListener>> listenerMap;

    private OrderComparator<EventListener> comparator = new OrderComparator<>();
    public EventManager() {
        this.listenerMap = Stream.of(EventType.values()).collect(Collectors.toMap(v -> v, v -> new LinkedList<>()));
    }

    public void addEventListener(EventType type, EventListener listener) {
        listenerMap.get(type).add(listener);
    }

    public void fireEvent(EventType type, Blade blade) {
        listenerMap.get(type).stream()
                .sorted(comparator)
                .forEach(listener -> listener.handleEvent(new Event(type, blade)));
    }

    public void fireEvent(EventType type) {
        fireEvent(type, null);
    }

}