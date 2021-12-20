package com.github.cosycode.common.util.reflex;

import com.github.cosycode.common.ext.hub.Throws;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <b>Description : </b> lambda 表达式工具栏
 * <p>
 * <b>created in </b> 2021/4/7
 *
 * @author CPF
 * @since 1.0
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class LambdaUtils {

    /**
     * 通过闭包代理函数式接口获取闭包代理类
     *
     * @param functional 函数接口对象
     * @return 函数接口对象所属的对象
     */
    public static Object getInstanceByFunctional(@NonNull Object functional) {
        final Field[] declaredFields = functional.getClass().getDeclaredFields();
        if (declaredFields.length < 1) {
            return null;
        }
        final Field declaredField = declaredFields[0];
        declaredField.setAccessible(true);
        return Throws.fun(functional, declaredField::get).runtimeExp().value();
    }

    /**
     * 若传入的参数是函数式接口实例对象, 且函数式接口实例是通过 对象名::方法名 初始化得到,
     * 则通过此方法, 可以得到其引用方法所属的对象.
     *
     * @param fn 函数式接口对象
     * @return 函数式接口对象指向的方法的类
     * @throws NoSuchMethodException 参数对象通过反射不能得到 writeReplace 方法
     * @throws InvocationTargetException 反射调用对象失败
     */
    public static SerializedLambda getSerializedLambda(Serializable fn) throws NoSuchMethodException, InvocationTargetException {
        final Method method = fn.getClass().getDeclaredMethod("writeReplace");
        final SerializedLambda lambda;
        try {
            method.setAccessible(Boolean.TRUE);
            lambda = (SerializedLambda) method.invoke(fn);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("反射调用对象的writeReplace方法出现异常, 关联对象: " + fn);
        }
        return lambda;
    }

}
