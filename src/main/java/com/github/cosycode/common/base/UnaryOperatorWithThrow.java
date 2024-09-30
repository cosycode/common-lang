package com.github.cosycode.common.base;

/**
 * <p>
 * <b>created in </b> 2020/6/29
 *
 * @author CPF
 * @see FunctionWithThrow
 * @see java.util.function.UnaryOperator
 * @since 1.0
 **/
@FunctionalInterface
public interface UnaryOperatorWithThrow<T, E extends Exception> extends FunctionWithThrow<T, T, E>{

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @param <T> the type of the input and output of the operator
     * @return a unary operator that always returns its input argument
     */
    static <T> UnaryOperatorWithThrow<T, Exception> identity() {
        return t -> t;
    }
}
