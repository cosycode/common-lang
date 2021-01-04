package com.github.cosycode.common.ext.hub;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <b>Description : </b> 全局单例池
 * <p>
 * <b>created in </b> 2020/6/12
 *
 * @author CPF
 * @since 1.0
 * @deprecated 1.1 replace by {@link SingletonPool}
 */
@Slf4j
@Deprecated
public class GlobalSingletonPool {

    private GlobalSingletonPool() {
    }

    /**
     * 存放单例
     */
    @Getter
    private static final Map<String, Object> singleTonPool = new ConcurrentHashMap<>();

    @Getter
    private static final Map<String, Supplier<?>> supplierMap = new ConcurrentHashMap<>();

    /**
     * 注册完成之后, 通过 get 方法获取, 如果 singleTonMap 中没有值, 则根据 supplier 创建一个对象, 存入 singleTonMap
     *
     * @param key      key
     * @param supplier 函数接口
     */
    public static synchronized void registerSupplier(String key, Supplier<?> supplier) {
        supplierMap.put(key, supplier);
    }

    /**
     * 如果singleTonMap中已有 key, 则抛出异常
     *
     * @param key    key
     * @param object 注册对象
     */
    public static synchronized void registerObject(String key, Object object) {
        if (singleTonPool.containsKey(key)) {
            throw new RuntimeException("单例对象中值不能重复注册 key: " + key);
        }
        singleTonPool.put(key, object);
    }

    public static Object remove(String key) {
        return singleTonPool.remove(key);
    }

    public static synchronized Object remove(String key, Consumer<Object> consumer) {
        final Object remove = singleTonPool.remove(key);
        if (remove != null) {
            consumer.accept(remove);
        }
        return remove;
    }

    public static Object removeSupplier(String key) {
        return supplierMap.remove(key);
    }

    public static synchronized Object removeSupplier(String key, Consumer<Object> consumer) {
        final Object remove = supplierMap.remove(key);
        if (remove != null) {
            consumer.accept(remove);
        }
        return remove;
    }

    public static Object get(String key) {
        Object o = singleTonPool.get(key);
        if (o != null) {
            return o;
        }
        final Supplier<?> supplier = supplierMap.get(key);
        if (supplier == null) {
            return null;
        }
        // 使用 ConcurrentHashMap 中的线程安全方法
        return singleTonPool.computeIfAbsent(key, k -> {
            log.error("单例初始化 => key: {}", k);
            final Object o1 = supplier.get();
            Objects.requireNonNull(o1, "不能为空");
            return o1;
        });
    }

    public static <T> T get(String key, Class<T> clazz) {
        Object obj = get(key);
        return obj == null ? null : clazz.cast(obj);
    }

    public static Object getWithNonNull(String key) {
        Object obj = get(key);
        if (obj == null) {
            throw new RuntimeException("单例对象获取失败, map中不包含此单例");
        }
        return obj;
    }

    public static <T> T getWithNonNull(String key, Class<T> clazz) {
        T t = get(key, clazz);
        if (t == null) {
            throw new RuntimeException("单例对象获取失败, map中不包含此单例");
        }
        return t;
    }

}
