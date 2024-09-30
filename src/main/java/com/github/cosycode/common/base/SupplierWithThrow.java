package com.github.cosycode.common.base;

/**
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
     */
    T get() throws E;
}
