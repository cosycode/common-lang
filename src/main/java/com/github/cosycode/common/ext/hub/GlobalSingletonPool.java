package com.github.cosycode.common.ext.hub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * <b>Description : </b> 全局单例池
 *
 * @author CPF
 * @date 2020/6/12 17:15
 */
public class GlobalSingletonPool {

    private GlobalSingletonPool(){}

    /**
     * 存放单例
     */
    private static Map<String, Object> singleTonMap = new ConcurrentHashMap<>();

    private static Map<String, Supplier<?>> supplierMap;

    /**
     * 注册完成之后, 通过 get 方法获取, 如果 singleTonMap 中没有值, 则根据 supplier 创建一个对象, 存入 singleTonMap
     *
     * @param key key
     * @param supplier 函数接口
     */
    public static synchronized void registerSupplier(String key, Supplier<?> supplier) {
        if (supplierMap == null) {
            supplierMap = new ConcurrentHashMap<>();
        }
        supplierMap.put(key, supplier);
    }

    /**
     * 如果singleTonMap中已有 key, 则抛出异常
     *
     * @param key key
     * @param object 注册对象
     */
    public static synchronized void registerObject(String key, Object object) {
        if (singleTonMap.containsKey(key)) {
            throw new RuntimeException("单例对象中值不能重复注册 key: " + key);
        }
        singleTonMap.put(key, object);
    }

    public static Object remove(String key) {
        return singleTonMap.remove(key);
    }

    @SuppressWarnings("all")
    public static Object get(String key) {
        Object o = singleTonMap.get(key);
        if (o == null && supplierMap != null && supplierMap.containsKey(key)) {
            final Supplier<?> supplier = supplierMap.get(key);
            if (supplier != null) {
                // 此处虽然锁的Map中的对象, 是安全的
                synchronized (supplier) {
                    if (!singleTonMap.containsKey(key)) {
                        o = supplier.get();
                        GlobalSingletonPool.registerObject(key, o);
                    }
                }
            }
        }
        return o;
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
        return clazz.cast(get(key));
    }

}
