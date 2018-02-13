package com.blade.kit;

/**
 * @author <a href="mailto:chenchen_839@126.com" target="_blank">ccqy66</a>
 */
public class Tuple2<E,T> {
    private E e;
    private T t;

    public Tuple2(E e,T t) {
        this.e = e;
        this.t = t;
    }
    public E _1() {
        return e;
    }
    public T _2() {
        return t;
    }
}
