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
package com.blade.kit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Collection kit
 *
 * @author biezhi
 * @date 2017/12/15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionKit {

    /**
     * Determines whether an array is empty
     *
     * @param array array object
     * @param <T>   array type
     * @return return array is empty
     */
    public static <T> boolean isEmpty(T[] array) {
        return null == array || array.length == 0;
    }

    /**
     * Determines whether an array is not empty
     *
     * @param array array object
     * @param <T>   array type
     * @return return array is not empty
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return null != array && array.length > 0;
    }

    /**
     * Determines whether an collection is empty
     *
     * @param collection collection object
     * @param <T>        collection type
     * @return return collection is empty
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return null == collection || collection.size() == 0;
    }

    /**
     * Determines whether an collection is not empty
     *
     * @param collection collection object
     * @param <T>        collection type
     * @return return collection is not empty
     */
    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return null != collection && collection.size() > 0;
    }

    /**
     * New HashMap
     *
     * @param <K> HashMap Key type
     * @param <V> HashMap Value type
     * @return return HashMap
     */
    public static <K, V> HashMap<K, V> newMap() {
        return new HashMap<>();
    }

    /**
     * New HashMap and initialCapacity
     *
     * @param initialCapacity initialCapacity
     * @param <K>             HashMap Key type
     * @param <V>             HashMap Value type
     * @return return HashMap
     */
    public static <K, V> HashMap<K, V> newMap(int initialCapacity) {
        return new HashMap<>(initialCapacity);
    }

    /**
     * New ConcurrentMap
     *
     * @param <K> ConcurrentMap Key type
     * @param <V> ConcurrentMap Value type
     * @return return ConcurrentMap
     */
    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return new ConcurrentHashMap<>();
    }

    /**
     * New ConcurrentMap and initialCapacity
     *
     * @param initialCapacity initialCapacity
     * @param <K>             ConcurrentMap Key type
     * @param <V>             ConcurrentMap Value type
     * @return return ConcurrentMap
     */
    public static <K, V> ConcurrentMap<K, V> newConcurrentMap(int initialCapacity) {
        return new ConcurrentHashMap<>(initialCapacity);
    }

    /**
     * New List and add values
     *
     * @param values list values
     * @param <T>    list type
     * @return return array list
     */
    @SafeVarargs
    public static <T> List<T> newLists(T... values) {
        if(null == values || values.length == 0){
            Assert.notNull(values, "values not is null.");
        }
        return Arrays.asList(values);
    }

    /**
     * New Set and add values
     *
     * @param values set values
     * @param <T>    set type
     * @return return HashSet
     */
    @SafeVarargs
    public static <T> Set<T> newSets(T... values) {
        if(null == values || values.length == 0){
            Assert.notNull(values, "values not is null.");
        }
        return new HashSet<>(Arrays.asList(values));
    }

}
