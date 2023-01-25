package com.github.cosycode.common.ext.hub;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * <b>Description : </b> 单例懒加载类, 方便单例类的创建
 * <p>
 * <b>created in </b> 2020/6/15
 *
 * @author CPF
 * @since 1.0
 */
public class LazySingleton<T> {

    /**
     * 单例模式仅仅保证引用可见性即可
     */
    @SuppressWarnings("java:S3077")
    private volatile T t;

    private final Supplier<T> supplier;

    public static <R> LazySingleton<R> of(Supplier<R> supplier) {
        return new LazySingleton<>(supplier);
    }

    private LazySingleton(Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "构造器不能为空");
        this.supplier = supplier;
    }

    /**
     * 双重锁单例
     *
     * @return 值
     */
    public T instance() {
        T tmp = t;
        if (tmp == null) {
            synchronized (this) {
                if (t == null) {
                    t = supplier.get();
                }
                tmp = t;
            }
        }
        return tmp;
    }

    /**
     * 破环当前实例(适用于想要重新生成实例的情况)
     *
     * @deprecated 取消该方法
     * @return 原来有实例为 true, 没有则返回 false
     */
    @Deprecated
    public boolean destroy() {
        if (t != null) {
            synchronized (this) {
                if (t != null) {
                    t = null;
                    return true;
                }
            }
        }
        return false;
    }

}
