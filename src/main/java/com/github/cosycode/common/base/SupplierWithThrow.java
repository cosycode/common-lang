package com.github.cosycode.common.base;

/**
 * <b>Description : </b> 带有抛出异常的 Supplier 接口
 * <p>
 * <b>created in </b> 2020/6/29
 *
 * @author CPF
 * @see java.util.function.Supplier
 * @since 1.0
 */
@FunctionalInterface
public interface SupplierWithThrow<T, E extends Throwable> {

    T get() throws E;
}
