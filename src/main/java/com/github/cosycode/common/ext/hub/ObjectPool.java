package com.github.cosycode.common.ext.hub;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/11/14
 * </p>
 *
 * @author pengfchen
 * @since 1.8
 **/
public class ObjectPool<T> {

    public final Queue<T> list = new ConcurrentLinkedQueue<>();

    private final Supplier<T> supplier;

    public ObjectPool(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T pollInstance() {
        T t = list.poll();
        if (t == null) {
            t = supplier.get();
        }
        return t;
    }

    public void pushInstance(T t) {
        list.add(t);
    }

}
