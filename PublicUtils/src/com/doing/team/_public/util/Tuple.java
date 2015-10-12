package com.doing.team._public.util;

/**
 * Created by liumingbin on 15/3/5.
 */
public class Tuple<X, Y, Z> {
    private final X x;
    private final Y y;
    private final Z z;

    public Tuple(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public X getFirst() {
        return x;
    }

    public Y getSecond() {
        return y;
    }

    public Z getThird() {
        return z;
    }

    public boolean equal(Tuple rhs) {
        return this.x == rhs.x && this.y == rhs.y && this.z == rhs.z;
    }
}