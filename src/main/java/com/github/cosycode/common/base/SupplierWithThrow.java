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
public interface SupplierWithThrow<T, E extends Exception> extends SerialFunctional{

    /**
     * Gets a result.
     *
     * @return a result
     * @throws E 函数式接口可以抛出的 Exception 类型
     */
    T get() throws E;
}
