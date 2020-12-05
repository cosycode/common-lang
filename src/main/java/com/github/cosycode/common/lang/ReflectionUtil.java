package com.github.cosycode.common.lang;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

/**
 * <b>Description : </b> 用于通用异常检查报错抛出
 *
 * @author CPF
 * @since 1.0
 * @date 2020/3/24
 */
@Slf4j
public class ReflectionUtil {

    private ReflectionUtil(){}

    /**
     * 创建一个实例
     */
    public static Object newInstance(Class<?> cls) {
        Object instance;
        try {
            instance = cls.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.error("new Instance failure", e);
            throw new RuntimeException(e);
        }
        return instance;
    }


}
