package com.github.cosycode.common.base;

/**
 * <b>Description : </b> 带有抛出异常的 Supplier 接口
 *
 * @author CPF
 * @since 1.0
 * @date 2020/6/29
 */
@FunctionalInterface
public interface SupplierWithThrow<T, E extends Throwable> {

    T get() throws E;
}
