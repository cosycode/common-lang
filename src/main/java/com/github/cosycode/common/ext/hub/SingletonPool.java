package com.github.cosycode.common.ext.hub;

import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * <b>Description : </b> 单例池
 * <p>
 * <b>created in </b> 2020/6/15
 *
 * @author CPF
 * @since 1.0
 */
public class SingletonPool {

    private SingletonPool() {
    }

    /**
     * 存放单例
     */
    @Getter
    private static final Map<String, Object> pool = new ConcurrentHashMap<>();

    /**
     * 注册完成之后, 通过 get 方法获取, 如果 singleTonMap 中没有值, 则根据 supplier 创建一个对象, 存入 singleTonMap
     *
     * @param key      key
     * @param supplier 函数接口
     */
    public static synchronized void registerSupplier(String key, Supplier<?> supplier) {
        pool.put(key, LazySingleton.of(supplier));
    }

    /**
     * 注册完成之后, 通过 get 方法获取, 如果 singleTonMap 中没有值, 则根据 supplier 创建一个对象, 存入 singleTonMap
     *
     * @param key      key
     * @param supplier 函数接口
     */
    public static synchronized void registerSupplierIfAbsent(String key, Supplier<?> supplier) {
        pool.putIfAbsent(key, LazySingleton.of(supplier));
    }

    /**
     * 如果singleTonMap中已有 key, 则抛出异常
     *
     * @param key    key
     * @param object 注册对象
     */
    public static synchronized void registerObject(String key, Object object) {
        pool.put(key, object);
    }

    /**
     * 如果singleTonMap中已有 key, 则抛出异常
     *
     * @param key    key
     * @param object 注册对象
     */
    public static synchronized void registerObjectIfAbsent(String key, Object object) {
        pool.putIfAbsent(key, object);
    }

    /**
     * 判断 单例池里面是否存在指定 key
     *
     * @param key key
     * @return true: 包含, false: 不包含
     */
    public static synchronized boolean containsKey(String key) {
        return pool.containsKey(key);
    }

    /**
     * 移除指定的单例
     *
     * @param key 标识
     * @return 移除的单例
     */
    public static Object remove(String key) {
        final Object o = pool.remove(key);
        if (o == null) {
            return null;
        }
        if (o instanceof LazySingleton) {
            return ((LazySingleton<?>) o).instance();
        }
        return o;
    }

    /**
     * 获取pool中的单例
     *
     * @param key 标识
     * @return pool中的单例
     */
    public static Object get(String key) {
        Object o = pool.get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof LazySingleton) {
            return ((LazySingleton<?>) o).instance();
        }
        return o;
    }

    /**
     * 获取pool中的单例
     *
     * @param key   标识
     * @param clazz 单例的预定类型
     * @param <T>   单例的预定类型(返回值类型)
     * @return pool中的单例
     */
    public static <T> T get(String key, Class<T> clazz) {
        Object obj = get(key);
        return obj == null ? null : clazz.cast(obj);
    }

    /**
     * 获取pool中的单例的 Optional 实例
     *
     * @param key   标识
     * @param clazz 单例的预定类型
     * @param <T>   单例的预定类型(返回值类型)
     * @return pool中的单例的 Optional 实例
     */
    public static <T> Optional<T> getOptional(String key, Class<T> clazz) {
        T t = get(key, clazz);
        return Optional.ofNullable(t);
    }

}
