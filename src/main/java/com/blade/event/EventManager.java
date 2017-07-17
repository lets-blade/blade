/**
 * Copyright (c) 2017, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.event;

import com.blade.Blade;
import com.blade.ioc.OrderComparator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventManager {

    private Map<EventType, List<EventListener>> listenerMap = null;
    private OrderComparator<EventListener>      comparator  = new OrderComparator<>();

    public EventManager() {
        this.listenerMap = Stream.of(EventType.values()).collect(Collectors.toMap(v -> v, v -> new LinkedList<>()));
    }

    public void addEventListener(EventType type, EventListener listener) {
        listenerMap.get(type).add(listener);
    }

    public void fireEvent(EventType type, Blade blade) {
        listenerMap.get(type).stream()
                .sorted(comparator)
                .forEach(listener -> listener.trigger(new Event(type, blade)));
    }

    public void fireEvent(EventType type) {
        fireEvent(type, null);
    }

}