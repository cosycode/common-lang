package com.github.cosycode.common.base;

import java.util.Date;
import java.util.function.Function;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2020/8/12
 */
public interface IValGetter<T> {
    
    T getVal();

    default <R> R get(Class<R> returnClass) {
        return returnClass.cast(getVal());
    }

    default String getString() {
        return (String) getVal();
    }

    default Date getDate() {
        return (Date) getVal();
    }

    default Integer getInteger() {
        return (Integer) getVal();
    }

    default T getDefault(T defaultObj) {
        T o = getVal();
        if (o == null) {
            return defaultObj;
        }
        return o;
    }

    default <R> R getDefault(Class<R> returnClass, R defaultObj){
        T o = getVal();
        if (o == null) {
            return defaultObj;
        }
        return returnClass.cast(o);
    }

    default <R> R get(Function<T, R> function){
        T o = getVal();
        return function.apply(o);
    }

}
