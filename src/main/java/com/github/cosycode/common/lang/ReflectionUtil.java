package com.github.cosycode.common.lang;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

/**
 * <b>Description : </b> 用于通用异常检查报错抛出
 * <p>
 * <b>created in </b> 2020/3/24
 *
 * @author CPF
 * @since 1.0
 */
@Slf4j
public class ReflectionUtil {

    private ReflectionUtil() {
    }

    /**
     * 创建一个实例
     *
     * @param cls 待创建的cls对象
     * @return 创建的实例
     */
    public static Object newInstance(Class<?> cls) {
        Object instance;
        try {
            instance = cls.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ActionExecException("new Instance failure", e);
        }
        return instance;
    }


}
