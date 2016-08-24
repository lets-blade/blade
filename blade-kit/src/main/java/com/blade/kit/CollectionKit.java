/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.kit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 有关集合处理的工具类，通过静态方法消除泛型编译警告。
 * <p>
 * 这个类中的每个方法都可以“安全”地处理 null ，而不会抛出 NullPointerException。
 * </p>
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class CollectionKit {
	
	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
	
	/**
     * new HashMap
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    /**
     * new HashMap and initialCapacity
     */
    public static <K, V> HashMap<K, V> newHashMap(int size) {
        return new HashMap<K, V>(size);
    }
    
    /**
     * new HashMap and initialCapacity
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<K, V>();
    }
    
    /**
     * new HashMap and initialCapacity
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(int size) {
        return new LinkedHashMap<K, V>(size);
    }
    
    /**
     * new concurrentHashMap
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<K, V>(DEFAULT_INITIAL_CAPACITY, 0.75f, DEFAULT_CONCURRENCY_LEVEL);
    }
    
    /**
     * new concurrentHashMap and initialCapacity
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int size) {
        return new ConcurrentHashMap<K, V>(size);
    }
    
    /**
     * new ArrayList
     */
    public static <T> ArrayList<T> newArrayList() {
        return new ArrayList<T>();
    }
    
    /**
     * new ArrayList and initialCapacity
     */
    public static <T> ArrayList<T> newArrayList(int size) {
        return new ArrayList<T>(size);
    }

    /**
     * new HashSet
     */
    public static <T> HashSet<T> newHashSet() {
        return new HashSet<T>();
    }

    /**
     * new HashSet and initialCapacity
     */
    public static <T> HashSet<T> newHashSet(int size) {
        return new HashSet<T>(size);
    }
    
    /**
     * new TreeSet
     */
    public static <T> TreeSet<T> newTreeSet() {
        return new TreeSet<T>();
    }
    
    /**
     * map sort
     */
    public static <K, V> Map<K, V> sortMap(Map<K, V> map, Comparator<Entry<K, V>> compator) {
        Map<K, V> result = new LinkedHashMap<K, V>();
        List<Entry<K, V>> entries = new ArrayList<Entry<K, V>>(map.entrySet());
        Collections.sort(entries, compator);
        for (Entry<K, V> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    /**
     * return map is empty
     */
    public static <K,V> boolean isEmpty(Map<K, V> map){
        return (null == map || map.isEmpty());
    }
    
    /**
     * return array is empty
     */
    public static <T> boolean isEmpty(T[] arr){
    	return null == arr || arr.length == 0;
    }
    
    /**
     * merge array
     * @param a
     * @param b
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <T> T[] concat(T[] a, T[] b) {
    	
        final int alen = a.length;
        final int blen = b.length;
        if (alen == 0) {
            return b;
        }
        if (blen == 0) {
            return a;
        }
        final T[] result = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), alen + blen);
        System.arraycopy(a, 0, result, 0, alen);
        System.arraycopy(b, 0, result, alen, blen);
        return result;
    }
    
    /**
     * 判断Map是否不为null和空{}
     * 
     * @param map ## @see Map
     * @return 如果不为空, 则返回true
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return (map != null) && (map.size() > 0);
    }

    /**
     * 判断Collection是否为null或空数组[]。
     * 
     * @param collection
     * @see Collection
     * @return 如果为空, 则返回true
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null) || (collection.size() == 0);
    }

    /**
     * 判断Collection是否不为null和空数组[]。
     * 
     * @param collection
     * @return 如果不为空, 则返回true
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return (collection != null) && (collection.size() > 0);
    }

    /**
     * 判断Enumeration是否有元素
     * 
     * @param enums ## @see Enumeration
     * @return 如果有元素, 则返回true
     */
    public static boolean hasItems(Enumeration<?> enums) {
        return (enums != null) && (enums.hasMoreElements());
    }

    /**
     * 判断Enumeration是否没有元素
     * 
     * @param enums ## @see Enumeration
     * @return 如果没有元素, 则返回true
     */
    public static boolean hasNotItems(Enumeration<?> enums) {
        return (enums == null) || (!enums.hasMoreElements());
    }

    /**
     * 判断Iterator是否有元素
     * 
     * @param iters ## @see Iterator
     * @return 如果有元素, 则返回true
     */
    public static boolean hasItems(Iterator<?> iters) {
        return (iters != null) && (iters.hasNext());
    }

    /**
     * 判断Iterator是否没有元素
     * 
     * @param iters ## @see Iterator
     * @return 如果没有元素, 则返回true
     */
    public static boolean hasNotItems(Iterator<?> iters) {
        return (iters != null) && (iters.hasNext());
    }

    /**
     * 创建ArrayList实例
     * 
     * @param <E>
     * @return ArrayList实例
     */
    public static <E> ArrayList<E> createArrayList() {
        return new ArrayList<E>();
    }

    /**
     * 创建ArrayList实例
     * 
     * @param <E>
     * @param initialCapacity 初始化容量
     * @return ArrayList实例
     */
    public static <E> ArrayList<E> createArrayList(int initialCapacity) {
        return new ArrayList<E>(initialCapacity);
    }

    /**
     * 创建ArrayList实例
     * 
     * @param <E>
     * @param collection 集合 @see Collection
     * @return ArrayList实例
     */
    public static <E> ArrayList<E> createArrayList(Collection<? extends E> collection) {
        if (collection == null) {
            return new ArrayList<E>();
        }

        return new ArrayList<E>(collection);
    }

    /**
     * 创建ArrayList实例
     * 
     * @param iter 迭代器 @see Iterable
     * @return ArrayList实例
     */
    public static <E> ArrayList<E> createArrayList(Iterable<? extends E> iter) {

        if (iter instanceof Collection<?>) {
            return new ArrayList<E>((Collection<? extends E>) iter);
        }

        ArrayList<E> list = new ArrayList<E>();

        iterableToCollection(iter, list);

        list.trimToSize();

        return list;
    }

    /**
     * 创建ArrayList实例
     * 
     * @param V 构建对象集
     * @return ArrayList实例
     */
    public static <T, V extends T> ArrayList<T> createArrayList(V...args) {
        if (args == null || args.length == 0) {
            return new ArrayList<T>();
        }

        ArrayList<T> list = new ArrayList<T>(args.length);

        for (V v : args) {
            list.add(v);
        }

        return list;

    }

    /**
     * 创建LinkedList实例
     * 
     * @param <E>
     * @return LinkedList实例
     */
    public static <E> LinkedList<E> createLinkedList() {
        return new LinkedList<E>();
    }

    /**
     * 创建LinkedList实例
     * 
     * @param collection 集合 @see Collection
     * @return LinkedList实例
     */
    public static <E> LinkedList<E> createLinkedList(Collection<? extends E> collection) {
        if (collection == null) {
            return new LinkedList<E>();
        }

        return new LinkedList<E>(collection);
    }

    /**
     * 创建LinkedList实例
     * 
     * @param Iterable 迭代器 @see Iterable
     * @return LinkedList实例
     */
    public static <T> LinkedList<T> createLinkedList(Iterable<? extends T> c) {
        LinkedList<T> list = new LinkedList<T>();

        iterableToCollection(c, list);

        return list;
    }

    /**
     * 创建LinkedList实例
     * 
     * @param V 构建对象集
     * @return LinkedList实例
     */
    public static <T, V extends T> LinkedList<T> createLinkedList(V...args) {
        LinkedList<T> list = new LinkedList<T>();

        if (args != null) {
            for (V v : args) {
                list.add(v);
            }
        }

        return list;
    }

    /**
     * 创建一个List。
     * <p>
     * 和{@code createArrayList(args)}不同，本方法会返回一个不可变长度的列表，且性能高于 {@code createArrayList(args)}。
     * </p>
     */
    public static <T> List<T> asList(T...args) {
        if (args == null || args.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.asList(args);
    }

    /**
     * 创建HashSet实例
     * 
     * @param <E>
     * @return HashSet实例
     */
    public static <E> HashSet<E> createHashSet() {
        return new HashSet<E>();
    }

    /**
     * 创建HashSet实例
     * 
     * @param <E>
     * @param initialCapacity 初始化容量
     * @return HashSet实例
     */
    public static <E> HashSet<E> createHashSet(int initialCapacity) {
        return new HashSet<E>(initialCapacity);
    }

    /**
     * 创建HashSet实例
     * 
     * @param collection 集合
     * @return HashSet实例
     */
    public static <E> HashSet<E> createHashSet(Collection<? extends E> collection) {
        if (collection == null) {
            return new HashSet<E>();
        }
        return new HashSet<E>(collection);
    }

    /**
     * 创建HashSet实例
     * 
     * @param args 传入参数
     * @return HashSet实例
     */
    public static <E, O extends E> HashSet<E> createHashSet(O...args) {
        if (args == null || args.length == 0) {
            return new HashSet<E>();
        }

        HashSet<E> set = new HashSet<E>(args.length);
        for (O o : args) {
            set.add(o);
        }

        return set;
    }

    /**
     * 创建一个HashSet。
     * 
     * @param iter 迭代器 @see Iterable
     * @return HashSet实例
     */
    public static <T> HashSet<T> createHashSet(Iterable<? extends T> iter) {
        HashSet<T> set;

        if (iter instanceof Collection<?>) {
            set = new HashSet<T>((Collection<? extends T>) iter);
        } else {
            set = new HashSet<T>();
            iterableToCollection(iter, set);
        }

        return set;
    }

    /**
     * 创建LinkedHashSet实例
     * 
     * @param <E>
     * @return LinkedHashSet实例
     */
    public static <E> LinkedHashSet<E> createLinkedHashSet() {
        return new LinkedHashSet<E>();
    }

    /** 创建一个LinkedHashSet。 */
    public static <T, V extends T> LinkedHashSet<T> createLinkedHashSet(V...args) {
        if (args == null || args.length == 0) {
            return new LinkedHashSet<T>();
        }
        LinkedHashSet<T> set = new LinkedHashSet<T>(args.length);

        for (V v : args) {
            set.add(v);
        }

        return set;
    }

    /** 创建一个LinkedHashSet。 */
    public static <T> LinkedHashSet<T> createLinkedHashSet(Iterable<? extends T> iter) {
        LinkedHashSet<T> set;

        if (iter instanceof Collection<?>) {
            set = new LinkedHashSet<T>((Collection<? extends T>) iter);
        } else {
            set = new LinkedHashSet<T>();
            iterableToCollection(iter, set);
        }

        return set;
    }

    /** 创建一个TreeSet。 */
    @SuppressWarnings("unchecked")
    public static <T, V extends T> TreeSet<T> createTreeSet(V...args) {
        return (TreeSet<T>) createTreeSet(null, args);
    }

    /** 创建一个TreeSet。 */
    public static <T> TreeSet<T> createTreeSet(Iterable<? extends T> c) {
        return createTreeSet(null, c);
    }

    /** 创建一个TreeSet。 */
    public static <T> TreeSet<T> createTreeSet(Comparator<? super T> comparator) {
        return new TreeSet<T>(comparator);
    }

    /** 创建一个TreeSet。 */
    public static <T, V extends T> TreeSet<T> createTreeSet(Comparator<? super T> comparator, V...args) {
        TreeSet<T> set = new TreeSet<T>(comparator);

        if (args != null) {
            for (V v : args) {
                set.add(v);
            }
        }

        return set;
    }

    /** 创建一个TreeSet。 */
    public static <T> TreeSet<T> createTreeSet(Comparator<? super T> comparator, Iterable<? extends T> c) {
        TreeSet<T> set = new TreeSet<T>(comparator);

        iterableToCollection(c, set);

        return set;
    }

    /**
     * 创建TreeSet实例
     * 
     * @param <E>
     * @param set 排序的散列 @see SortedSet
     * @return TreeSet实例
     */
    public static <E> TreeSet<E> createTreeSet(SortedSet<E> set) {
        if (set == null) {
            return new TreeSet<E>();
        }

        return new TreeSet<E>(set);
    }

    /**
     * 创建HashMap实例
     * 
     * @param <K>
     * @param <V>
     * @return HashMap实例
     */
    public static <K, V> HashMap<K, V> createHashMap() {
        return new HashMap<K, V>();
    }

    /**
     * 创建HashMap实例
     * 
     * @param <K>
     * @param <V>
     * @param initialCapacity 初始化容量
     * @return HashMap实例
     */
    public static <K, V> HashMap<K, V> createHashMap(int initialCapacity) {
        return new HashMap<K, V>(initialCapacity);
    }

    /**
     * 创建HashMap实例
     * 
     * @param <K>
     * @param <V>
     * @param initialCapacity 初始化容量
     * @param loadFactor 加载因子
     * @return HashMap实例
     */
    public static <K, V> HashMap<K, V> createHashMap(int initialCapacity, float loadFactor) {
        return new HashMap<K, V>(initialCapacity, loadFactor);
    }

    public static <K, V> HashMap<K, V> synchronizedMap() {
        return (HashMap<K, V>) Collections.synchronizedMap(new HashMap<K, V>());
    }

    /**
     * 创建HashMap实例
     * 
     * @param <K>
     * @param <V>
     * @param map 映射表 @see Map
     * @return HashMap实例
     */
    public static <K, V> HashMap<K, V> createHashMap(Map<? extends K, ? extends V> map) {
        return new HashMap<K, V>(map);
    }

    /**
     * 创建LinkedHashMap实例
     * 
     * @param <K>
     * @param <V>
     * @return LinkedHashMap实例
     */
    public static <K, V> LinkedHashMap<K, V> createLinkedHashMap() {
        return new LinkedHashMap<K, V>();
    }

    /**
     * 创建LinkedHashMap实例
     * 
     * @param initialCapacity 初始化容量
     * @return LinkedHashMap实例
     */
    public static <K, V> LinkedHashMap<K, V> createLinkedHashMap(int initialCapacity) {
        return new LinkedHashMap<K, V>(initialCapacity);
    }

    /**
     * 创建LinkedHashMap实例
     * 
     * @param <K>
     * @param <V>
     * @param initialCapacity 初始化容量
     * @param loadFactor 加载因子
     * @return LinkedHashMap实例
     */
    public static <K, V> LinkedHashMap<K, V> createLinkedHashMap(int initialCapacity, float loadFactor) {
        return new LinkedHashMap<K, V>(initialCapacity, loadFactor);
    }

    /**
     * 创建LinkedHashMap实例
     * 
     * @param <K>
     * @param <V>
     * @param map 映射表 @see Map
     * @return HashMap实例
     */
    public static <K, V> LinkedHashMap<K, V> createLinkedHashMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return new LinkedHashMap<K, V>();
        }

        return new LinkedHashMap<K, V>(map);
    }

    /**
     * 创建ConcurrentMap实例
     * 
     * @param <K>
     * @param <V>
     * @return ConcurrentMap实例
     */
    public static <K, V> ConcurrentMap<K, V> createConcurrentMap() {
        return new ConcurrentHashMap<K, V>();
    }

    /**
     * 创建ConcurrentMap实例
     * 
     * @param <K>
     * @param <V>
     * @param map 映射表 @see Map
     * @return ConcurrentMap实例
     */
    public static <K, V> ConcurrentMap<K, V> createConcurrentMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return null;
        }

        return new ConcurrentHashMap<K, V>(map);
    }

    /**
     * 创建ConcurrentMap实例
     * 
     * @param <K>
     * @param <V>
     * @param initialCapacity 初始化容量
     * @return ConcurrentMap实例
     */
    public static <K, V> ConcurrentMap<K, V> createConcurrentMap(int initialCapacity) {
        return new ConcurrentHashMap<K, V>(initialCapacity);
    }

    /**
     * 创建ConcurrentMap实例
     * 
     * @param <K>
     * @param <V>
     * @param initialCapacity 初始化容量
     * @param loadFactor 加载因子
     * @return ConcurrentMap实例
     */
    public static <K, V> ConcurrentMap<K, V> createConcurrentMap(int initialCapacity, float loadFactor) {
        return new ConcurrentHashMap<K, V>(initialCapacity, loadFactor);
    }

    private static <E> void iterableToCollection(Iterable<? extends E> iter, Collection<E> list) {
        if (iter == null) {
            return;
        }

        for (E element : iter) {
            list.add(element);
        }
    }

    public static <E extends Enum<E>> EnumSet<E> createEnumSet(Collection<E> c) {
        if (c == null) {
            return null;
        }

        return EnumSet.copyOf(c);
    }

    public static <E extends Enum<E>> EnumSet<E> createEnumSet(Class<E> elementType) {
        if (elementType == null) {
            return null;
        }

        return EnumSet.allOf(elementType);
    }

    /**
     * 创建TreeMap实例
     * 
     * @param <K>
     * @param <V>
     * @return TreeMap实例
     */
    public static <K, V> TreeMap<K, V> createTreeMap() {
        return new TreeMap<K, V>();
    }

    /**
     * 创建TreeMap实例
     * 
     * @param <K>
     * @param <V>
     * @param comparator 比较器 @see Comparator
     * @return TreeMap实例
     */
    public static <K, V> TreeMap<K, V> createTreeMap(Comparator<? super K> comparator) {
        if (comparator == null) {
            return null;
        }

        return new TreeMap<K, V>(comparator);
    }

    /**
     * 创建TreeMap实例
     * 
     * @param <K>
     * @param <V>
     * @param map 映射表 @see Map
     * @return TreeMap实例
     */
    public static <K, V> TreeMap<K, V> createTreeMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return null;
        }

        return new TreeMap<K, V>(map);
    }

    /**
     * 创建TreeMap实例
     * 
     * @param <K>
     * @param <V>
     * @param map 排序的映射表 @see Map
     * @return TreeMap实例
     */
    public static <K, V> TreeMap<K, V> createTreeMap(SortedMap<K, ? extends V> map) {
        if (map == null) {
            return null;
        }

        return new TreeMap<K, V>(map);
    }

    /**
     * 创建WeakHashMap实例
     * 
     * @param <K>
     * @param <V>
     * @return WeakHashMap实例
     */
    public static <K, V> WeakHashMap<K, V> createWeakHashMap() {
        return new WeakHashMap<K, V>();
    }

    /**
     * 创建WeakHashMap实例
     * 
     * @param <K>
     * @param <V>
     * @param initialCapacity 初始化容量
     * @return WeakHashMap实例
     */
    public static <K, V> WeakHashMap<K, V> createWeakHashMap(int initialCapacity) {
        return new WeakHashMap<K, V>(initialCapacity);
    }

    /**
     * 创建WeakHashMap实例
     * 
     * @param <K>
     * @param <V>
     * @param map 映射表 @see Map
     * @return WeakHashMap实例
     */
    public static <K, V> WeakHashMap<K, V> createWeakHashMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return new WeakHashMap<K, V>();
        }

        return new WeakHashMap<K, V>(map);
    }

    /**
     * 创建WeakHashMap实例
     * 
     * @param <K>
     * @param <V>
     * @param initialCapacity 初始化容量
     * @param loadFactor 加载因子
     * @return WeakHashMap实例
     */
    public static <K, V> WeakHashMap<K, V> createWeakHashMap(int initialCapacity, float loadFactor) {
        return new WeakHashMap<K, V>(initialCapacity, loadFactor);
    }

    /**
     * 创建IdentityHashMap实例
     * 
     * @param <K>
     * @param <V>
     * @return IdentityHashMap实例
     */
    public static <K, V> IdentityHashMap<K, V> createIdentityHashMap() {
        return new IdentityHashMap<K, V>();
    }

    /**
     * 创建IdentityHashMap实例
     * 
     * @param <K>
     * @param <V>
     * @param initialCapacity 初始化容量
     * @return IdentityHashMap实例
     */
    public static <K, V> IdentityHashMap<K, V> createIdentityHashMap(int initialCapacity) {
        return new IdentityHashMap<K, V>(initialCapacity);
    }

    /**
     * 创建IdentityHashMap实例
     * 
     * @param <K>
     * @param <V>
     * @param map 映射表 @see Map
     * @return IdentityHashMap实例
     */
    public static <K, V> IdentityHashMap<K, V> createIdentityHashMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return null;
        }

        return new IdentityHashMap<K, V>(map);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> createEnumMap(Class<K> keyType) {
        if (keyType == null) {
            return null;
        }

        return new EnumMap<K, V>(keyType);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> createEnumMap(Map<K, ? extends V> map) {
        if (map == null) {
            return null;
        }

        return new EnumMap<K, V>(map);
    }

    /**
     * 创建PriorityQueue实例
     * 
     * @param <E>
     * @return PriorityQueue实例
     */
    public static <E> PriorityQueue<E> createPriorityQueue() {
        return new PriorityQueue<E>();
    }

    /**
     * 创建PriorityQueue实例
     * 
     * @param <E>
     * @param initialCapacity 初始化容量
     * @return PriorityQueue实例
     */
    public static <E> PriorityQueue<E> createPriorityQueue(int initialCapacity) {
        return new PriorityQueue<E>(initialCapacity);
    }

    /**
     * 创建PriorityQueue实例
     * 
     * @param <E>
     * @param collection 集合 @see Collection
     * @return PriorityQueue实例
     */
    public static <E> PriorityQueue<E> createPriorityQueue(Collection<? extends E> collection) {
        if (collection == null) {
            return null;
        }

        return new PriorityQueue<E>(collection);
    }

    /**
     * 创建PriorityQueue实例
     * 
     * @param <E>
     * @param initialCapacity 初始化容量
     * @param comparator 比较器 @see Comparator
     * @return PriorityQueue实例
     */
    public static <E> PriorityQueue<E> createPriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        if (comparator == null) {
            return new PriorityQueue<E>(initialCapacity);
        }

        return new PriorityQueue<E>(initialCapacity, comparator);
    }

    /**
     * 创建PriorityQueue实例
     * 
     * @param <E>
     * @param queue 队列 @see PriorityQueue
     * @return PriorityQueue实例
     */
    public static <E> PriorityQueue<E> createPriorityQueue(PriorityQueue<? extends E> queue) {
        if (queue == null) {
            return null;
        }

        return new PriorityQueue<E>(queue);
    }

    /**
     * 创建PriorityQueue实例
     * 
     * @param <E>
     * @param set 排序的散列 @see SortedSet
     * @return PriorityQueue实例
     */
    public static <E> PriorityQueue<E> createPriorityQueue(SortedSet<? extends E> set) {
        if (set == null) {
            return null;
        }

        return new PriorityQueue<E>(set);
    }

    /**
     * 创建ArrayDeque实例
     * 
     * @param <E>
     * @return ArrayDeque实例
     */
    public static <E> ArrayDeque<E> createArrayDeque() {
        return new ArrayDeque<E>();
    }

    /**
     * 创建ArrayDeque实例
     * 
     * @param <E>
     * @param collection 集合 @see Collection
     * @return ArrayDeque实例
     */
    public static <E> ArrayDeque<E> createArrayDeque(Collection<? extends E> collection) {
        if (collection == null) {
            return null;
        }

        return new ArrayDeque<E>(collection);
    }

    /**
     * 创建ArrayDeque实例
     * 
     * @param <E>
     * @param initialCapacity 初始化容量
     * @return ArrayDeque实例
     */
    public static <E> ArrayDeque<E> createArrayDeque(int initialCapacity) {
        return new ArrayDeque<E>(initialCapacity);
    }

    /**
     * 创建BitSet实例
     * 
     * @param <E>
     * @return BitSet实例
     */
    public static <E> BitSet createBitSet() {
        return new BitSet();
    }

    /**
     * 创建BitSet实例
     * 
     * @param <E>
     * @param initialCapacity 初始化容量
     * @return BitSet实例
     */
    public static <E> BitSet createBitSet(int initialCapacity) {
        return new BitSet();
    }

    /**
     * 创建ConcurrentSkipListMap实例
     * 
     * @param <K>
     * @param <V>
     * @return ConcurrentSkipListMap实例
     */
    public static <K, V> ConcurrentSkipListMap<K, V> createConcurrentSkipListMap() {
        return new ConcurrentSkipListMap<K, V>();
    }

    /**
     * 创建ConcurrentSkipListMap实例
     * 
     * @param <K>
     * @param <V>
     * @param comparator 比较器 @see Comparator
     * @return ConcurrentSkipListMap实例
     */
    public static <K, V> ConcurrentSkipListMap<K, V> createConcurrentSkipListMap(Comparator<? super K> comparator) {
        if (comparator == null) {
            return new ConcurrentSkipListMap<K, V>();
        }

        return new ConcurrentSkipListMap<K, V>(comparator);
    }

    /**
     * 创建ConcurrentSkipListMap实例
     * 
     * @param <K>
     * @param <V>
     * @param map 映射表 @see Map
     * @return ConcurrentSkipListMap实例
     */
    public static <K, V> ConcurrentSkipListMap<K, V> createConcurrentSkipListMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return new ConcurrentSkipListMap<K, V>();
        }

        return new ConcurrentSkipListMap<K, V>(map);
    }

    /**
     * 创建ConcurrentSkipListMap实例
     * 
     * @param <K>
     * @param <V>
     * @param map 排序的映射表 @see SortedMap
     * @return ConcurrentSkipListMap实例
     */
    public static <K, V> ConcurrentSkipListMap<K, V> createConcurrentSkipListMap(SortedMap<? extends K, ? extends V> map) {
        if (map == null) {
            return new ConcurrentSkipListMap<K, V>();
        }

        return new ConcurrentSkipListMap<K, V>(map);
    }

    /**
     * 创建ConcurrentSkipListSet实例
     * 
     * @param <E>	泛型	
     * @return ConcurrentSkipListSet实例
     */
    public static <E> ConcurrentSkipListSet<E> createConcurrentSkipListSet() {
        return new ConcurrentSkipListSet<E>();
    }

    /**
     * 创建ConcurrentSkipListSet实例
     * 
     * @param <E>	泛型
     * @param collection 集合 @see Collection
     * @return ConcurrentSkipListSet实例
     */
    public static <E> ConcurrentSkipListSet<E> createConcurrentSkipListSet(Collection<? extends E> collection) {
        if (collection == null) {
            return new ConcurrentSkipListSet<E>();
        }

        return new ConcurrentSkipListSet<E>(collection);
    }

    /**
     * 创建ConcurrentSkipListSet实例
     * 
     * @param <E>	泛型
     * @param comparator 比较器 @see Comparator
     * @return ConcurrentSkipListSet实例
     */
    public static <E> ConcurrentSkipListSet<E> createConcurrentSkipListSet(Comparator<? super E> comparator) {
        if (comparator == null) {
            return new ConcurrentSkipListSet<E>();
        }

        return new ConcurrentSkipListSet<E>(comparator);
    }

    /**
     * 创建ConcurrentSkipListSet实例
     * 
     * @param <E>	泛型
     * @param set 可排序的散列 @see SortedSet
     * @return ConcurrentSkipListSet实例
     */
    public static <E> ConcurrentSkipListSet<E> createConcurrentSkipListSet(SortedSet<E> set) {
        if (set == null) {
            return new ConcurrentSkipListSet<E>();
        }

        return new ConcurrentSkipListSet<E>(set);
    }

    /**
     * 创建ConcurrentLinkedQueue实例
     * 
     * @param <E>	泛型
     * @return ConcurrentLinkedQueue实例
     */
    public static <E> Queue<E> createConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue<E>();
    }

    /**
     * 创建ConcurrentLinkedQueue实例
     * 
     * @param <E>	泛型
     * @param collection 集合 @see Collection
     * @return ConcurrentLinkedQueue实例
     */
    public static <E> Queue<E> createConcurrentLinkedQueue(Collection<? extends E> collection) {
        if (collection == null) {
            return new ConcurrentLinkedQueue<E>();
        }

        return new ConcurrentLinkedQueue<E>(collection);
    }

    /**
     * 创建CopyOnWriteArrayList实例
     * 
     * @param <E>	泛型
     * @return CopyOnWriteArrayList实例
     */
    public static <E> CopyOnWriteArrayList<E> createCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<E>();
    }

    /**
     * 创建CopyOnWriteArrayList实例
     * 
     * @param <E>	泛型
     * @param collection 集合 @see Collection
     * 
     * @return CopyOnWriteArrayList实例
     */
    public static <E> CopyOnWriteArrayList<E> createCopyOnWriteArrayList(Collection<? extends E> collection) {
        if (collection == null) {
            return new CopyOnWriteArrayList<E>();
        }

        return new CopyOnWriteArrayList<E>();
    }

    /**
     * 创建CopyOnWriteArrayList实例
     * 
     * @param <E>	泛型
     * @param toCopyIn 创建一个保存给定数组的副本的数组
     * 
     * @return CopyOnWriteArrayList实例
     */
    public static <E> CopyOnWriteArrayList<E> createCopyOnWriteArrayList(E[] toCopyIn) {
        if (toCopyIn == null) {
            return new CopyOnWriteArrayList<E>();
        }

        return new CopyOnWriteArrayList<E>(toCopyIn);
    }

    /**
     * 创建CopyOnWriteArraySet实例
     * 
     * @param <E>	泛型
     * @return CopyOnWriteArraySet实例
     */
    public static <E> CopyOnWriteArraySet<E> createCopyOnWriteArraySet() {
        return new CopyOnWriteArraySet<E>();
    }

    /**
     * 创建CopyOnWriteArraySet实例
     * 
     * @param <E>	泛型
     * @param collection 集合 @see Collection
     * 
     * @return CopyOnWriteArraySet实例
     */
    public static <E> CopyOnWriteArraySet<E> createCopyOnWriteArraySet(Collection<? extends E> collection) {
        return new CopyOnWriteArraySet<E>();
    }

    public static <E> BlockingQueue<E> createLinkedBlockingQueue() {
        return new LinkedBlockingQueue<E>();
    }

    public static <E> BlockingQueue<E> createLinkedBlockingQueue(int capacity) {
        return new LinkedBlockingQueue<E>(capacity);
    }

    public static <E> BlockingQueue<E> createLinkedBlockingQueue(Collection<? extends E> collection) {
        if (collection == null) {
            return new LinkedBlockingQueue<E>();
        }

        return new LinkedBlockingQueue<E>(collection);
    }

    // ==========================================================================
    // 常用转换。
    // ==========================================================================
    /**
	 * 字符串数组去重复
	 * @param arr
	 * @return
	 */
	public static String[] arrayToHeavy(String[] arr){
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < arr.length; i++) {
			set.add(arr[i]);
		}
		String[] a = (String[]) set.toArray(new String[set.size()]);
		return a;
	}
	
	/**
	 * list去重复
	 * @param list
	 * @return
	 */
	public static <T> List<T> listToHeavy(List<T> list){
		Set<T> set = new HashSet<T>(list);
		list.clear();
		list.addAll(set);
		return list;
	}
	

    // ==========================================================================
    // 集合运算。
    // ==========================================================================

    /** 集合交集 */
    public static final <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        if (set1 == null || set2 == null) {
            return null;
        }

        if (set1.isEmpty() || set2.isEmpty()) {
            return Collections.emptySet();
        }

        Set<T> result = CollectionKit.createHashSet();
        Set<T> smaller = (set1.size() > set2.size()) ? set2 : set1;
        Set<T> bigger = (smaller == set2) ? set1 : set2;

        for (T value : smaller) {
            if (bigger.contains(value)) {
                result.add(value);
            }
        }
        return result;
    }

    public static <T> Set<T> subtract(Set<T> set1, Set<T> set2) {
        if (set1 == null || set2 == null) {
            return null;
        }
        Set<T> result = createHashSet(set1);
        result.removeAll(set2);
        return result;
    }

    public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
        if (isEmpty(set1)) {
            return set2;
        }
        if (isEmpty(set2)) {
            return set1;
        }

        Set<T> result = createHashSet(set1);
        result.addAll(set2);
        return result;
    }

    /** 连接 */
    public static <T> List<? extends T> concatSuper(List<? extends T> collection1, List<? extends T> collection2) {
        if (isEmpty(collection1)) {
            return collection2;
        }
        if (isEmpty(collection2)) {
            return collection1;
        }
        List<T> result = createArrayList(collection1.size() + collection2.size());
        result.addAll(collection1);
        result.addAll(collection2);
        return result;

    }

    /** 连接 */
    public static <T> List<T> concat(List<T> collection1, List<T> collection2) {
        if (isEmpty(collection1)) {
            return collection2;
        }
        if (isEmpty(collection2)) {
            return collection1;
        }

        collection1.addAll(collection2);
        return collection1;

    }

    // FIXME createCollections add DataFilter implement

}