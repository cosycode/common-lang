package com.github.cosycode.common.base;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @see FunctionWithThrow
 * @since 1.0
 * @date 2020/6/29
 **/
@FunctionalInterface
public interface UnaryOperatorWithThrow<T, E extends Throwable> extends FunctionWithThrow<T, T, E> {

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @param <T> the type of the input and output of the operator
     * @return a unary operator that always returns its input argument
     */
    static <T> UnaryOperatorWithThrow<T, Throwable> identity() {
        return t -> t;
    }
}
