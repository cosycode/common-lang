package com.github.cosycode.common.base;

import java.util.Objects;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2020/11/18
 **/
@FunctionalInterface
public interface FunctionWithThrow<T, R, E extends Throwable> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws E;

    /**
     * @see #compose(java.util.function.Function)
     */
    default <V> FunctionWithThrow<V, R, E> compose(java.util.function.Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    /**
     * @see #compose(java.util.function.Function)
     */
    default <V> FunctionWithThrow<T, V, E> andThen(FunctionWithThrow<? super R, ? extends V, E> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @param <T> the type of the input and output of the operator
     * @return a unary operator that always returns its input argument
     */
    static <T> FunctionWithThrow<T, T, Throwable> identity() {
        return t -> t;
    }
}
