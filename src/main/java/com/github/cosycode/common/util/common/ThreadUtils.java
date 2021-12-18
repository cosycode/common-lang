package com.github.cosycode.common.util.common;

import com.github.cosycode.common.lang.ShouldNotHappenException;
import com.github.cosycode.common.util.reflex.ReflexUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * <b>Description : </b> 线程工具类
 * <p>
 * <b>created in </b> 2021/1/20
 *
 * @author CPF
 * @since 1.2
 **/
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadUtils {

    /**
     * 获取 操作线程 所在的 线程组中 的 所有存活的线程
     *
     * @param thread 操作线程对象
     * @return 操作线程所在线程组中的 所有存活的线程
     */
    @SuppressWarnings("java:S3014")
    public static Thread[] getActiveThreadArrayInCurGroup(Thread thread) {
        final ThreadGroup threadGroup = thread.getThreadGroup();
        Thread[] lstThreads = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(lstThreads);
        return lstThreads;
    }

    /**
     * 获取 操作线程 的 ThreadLocalMap 对象, 并将其中的值以 (key: ThreadLocal, val: value) 封装进一个 HashMap
     *
     * @param thread 操作线程
     * @param <T> ThreadLocal 存值类型
     * @return 用 thread 对应的 ThreadLocalMap 封装的 Map<ThreadLocal<T>, T>
     */
    @SuppressWarnings({"java:S3011", "unchecked"})
    public static <T> Map<ThreadLocal<T>, T> getThreadLocalMap(Thread thread) {
        Map<ThreadLocal<T>, T> threadLocals = new HashMap<>();
        try {
            final Object threadLocalMap = ReflexUtils.getAttributeFromObject(Thread.class, thread, "threadLocals");
            if (threadLocalMap == null) {
                return Collections.emptyMap();
            }
            final Object[] table = (Object[]) ReflexUtils.getAttributeFromObject(threadLocalMap, "table");
            for (Object entry : table) {
                if (entry != null) {
                    WeakReference<ThreadLocal<T>> threadLocalRef = (WeakReference<ThreadLocal<T>>) entry;
                    ThreadLocal<T> threadLocal = threadLocalRef.get();
                    if (threadLocal != null) {
                        final Object value = ReflexUtils.getAttributeFromObject(entry, "value");
                        threadLocals.put(threadLocal, (T) value);
                    }
                }
            }
        } catch (NoSuchFieldException e) {
            throw new ShouldNotHappenException("一般情况不应该没有指定字段", e);
        }
        return threadLocals;
    }

    /**
     * 同步多线程处理, 使用 consumer 消费 tList 中的数据;
     * 如果 threadNum <= 0, 则使用stream中的并行流方式
     * 如果 threadNum == 1, 直接使用for循环处理数据
     * 如果 threadNum > 1, 则建立一个线程池处理数据 Executors.newFixedThreadPool(threadNum) 处理数据, 如果待处理列表数量小于线程池数量, 那么建立建立和待处理列表数量同样多的线程
     *
     * @param collection 待处理的集合(不允许为空)
     * @param consumer   处理数据的方法(不允许为空)
     * @param threadNum  线程池数量, 具体处理方式看方法说明
     * @param <T>        待处理的列表对象类型
     */
    public <T> void multithreading(@NonNull Collection<T> collection, @NonNull Consumer<T> consumer, int threadNum) {
        if (collection.isEmpty()) {
            return;
        }
        if (threadNum > 1) {
            // 新建 Math.min(threadNum, tList.size()) 个线程的线程池, 如果待处理列表数量小于线程池数量, 那么建立那么多的线程则没有必要
            ExecutorService executor = Executors.newFixedThreadPool(Math.min(threadNum, collection.size()));
            // 循环
            for (final T t : collection) {
                executor.submit(() -> consumer.accept(t));
            }
            executor.shutdown();
            while (true) {
                try {
                    if (executor.awaitTermination(100, TimeUnit.MICROSECONDS)) {
                        // 线程池结束时调用
                        break;
                    }
                } catch (InterruptedException e) {
                    log.error("线程池指定发生异常", e);
                    Thread.currentThread().interrupt();
                }
            }
        } else if (threadNum == 1) {
            collection.forEach(consumer);
        } else {
            collection.stream().parallel().forEach(consumer);
        }
    }

}
