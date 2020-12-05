package com.github.cosycode.common.base;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2020/12/5
 **/
public interface IMapGetter<K, V> extends Map<K, V> {

    default <T> T get(K key, Class<T> returnClass) {
        return returnClass.cast(get(key));
    }

    default String getString(K key) {
        return (String) get(key);
    }

    default Date getDate(K key) {
        return (Date) get(key);
    }

    default Integer getInteger(K key) {
        return (Integer) get(key);
    }

    default Object getDefault(K key, Object defaultObj) {
        Object o = get(key);
        if (o == null) {
            return defaultObj;
        }
        return o;
    }

    default <T> T getDefault(K key, Class<T> returnClass, T defaultObj){
        Object o = get(key);
        if (o == null) {
            return defaultObj;
        }
        return returnClass.cast(o);
    }

    default <R> R get(K key, Function<Object, R> function){
        Object o = get(key);
        return function.apply(o);
    }

}
