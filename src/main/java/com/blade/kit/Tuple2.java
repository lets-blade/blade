package com.blade.kit;

import java.util.Objects;

/**
 * Tuple2
 *
 * @author <a href="mailto:chenchen_839@126.com" target="_blank">ccqy66</a>
 */
public class Tuple2<K, V> {

    private K k;
    private V v;

    public Tuple2(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getK() {
        return k;
    }

    public V getV() {
        return v;
    }

    public K _1() {
        return k;
    }

    public V _2() {
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
        return Objects.equals(k, tuple2.k) &&
                Objects.equals(v, tuple2.v);
    }

    @Override
    public int hashCode() {
        return Objects.hash(k, v);
    }

}
