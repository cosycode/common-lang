package com.github.cosycode.common.override.java.juc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <b>Description : </b>
 * <p>
 * <p> 由于有一个需求, 我需要一个能够批量存储和提取的缓存队列, 但是 java.util.concurrent.ArrayBlockingQueue, 无法实现这个需求,
 * <p> 而且 java.util.concurrent.ArrayBlockingQueue 是一个 final 类, 无法继承, 所以只好将 ArrayBlockingQueue 代码全部拷贝了出来, 并加了几个方法
 * <b>created in </b> 2021/1/20
 *
 * @author CPF
 * @since 1.2
 **/
public class ArrayBatchBlockingQueue<E> extends ArrayBlockingQueueCopy<E> {
    /**
     * Creates an {@code ArrayBlockingQueue} with the given (fixed)
     * capacity and default access policy.
     *
     * @param capacity the capacity of this queue
     * @throws IllegalArgumentException if {@code capacity < 1}
     */
    public ArrayBatchBlockingQueue(int capacity) {
        super(capacity);
    }

    /**
     * Creates an {@code ArrayBlockingQueue} with the given (fixed)
     * capacity and the specified access policy.
     *
     * @param capacity the capacity of this queue
     * @param fair     if {@code true} then queue accesses for threads blocked
     *                 on insertion or removal, are processed in FIFO order;
     *                 if {@code false} the access order is unspecified.
     * @throws IllegalArgumentException if {@code capacity < 1}
     */
    public ArrayBatchBlockingQueue(int capacity, boolean fair) {
        super(capacity, fair);
    }

    /**
     * Creates an {@code ArrayBlockingQueue} with the given (fixed)
     * capacity, the specified access policy and initially containing the
     * elements of the given collection,
     * added in traversal order of the collection's iterator.
     *
     * @param capacity the capacity of this queue
     * @param fair     if {@code true} then queue accesses for threads blocked
     *                 on insertion or removal, are processed in FIFO order;
     *                 if {@code false} the access order is unspecified.
     * @param c        the collection of elements to initially contain
     * @throws IllegalArgumentException if {@code capacity} is less than
     *                                  {@code c.size()}, or less than 1.
     * @throws NullPointerException     if the specified collection or any
     *                                  of its elements are null
     */
    public ArrayBatchBlockingQueue(int capacity, boolean fair, Collection<E> c) {
        super(capacity, fair, c);
    }

    /**
     * 一次性提取指定数量的列表对象, 如果没有那么多, 则取可以取出的最小量
     *
     * @param cnt 最大取出数量
     * @return 取出的列表
     * @throws InterruptedException 加锁异常
     */
    public List<E> takeBatch(int cnt) throws InterruptedException {
        final ReentrantLock lock = super.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            // 确定提取数据条数
            final int deCount = Math.min(cnt, count);
            final List<E> list = new ArrayList<>(deCount);
            for (int i = 0; i < deCount; i++) {
                list.add(dequeue());
            }
            return list;
        } finally {
            lock.unlock();
        }
    }

}
