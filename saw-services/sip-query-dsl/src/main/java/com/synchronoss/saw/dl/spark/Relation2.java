package com.synchronoss.saw.dl.spark;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class Relation2<A ,B> {

    public final A _1;
    public final B _2;

    public Relation2(A _1, B _2) {
        this._1 = _1;
        this._2 = _2;
    }

    /**
     * Gets _1
     *
     * @return value of _1
     */
    public A _1(){
        return _1;
    }

    /**
     * Gets _2
     *
     * @return value of _2
     */
    public B _2() {
        return _2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relation2<?, ?> relation = (Relation2<?, ?>) o;
        if (!_1.equals(relation._1)) return false;
        return _2.equals(relation._2);
    }

    @Override
    public int hashCode() {
        return  new HashCodeBuilder().append(_1).append(_2).toHashCode();
    }

}
