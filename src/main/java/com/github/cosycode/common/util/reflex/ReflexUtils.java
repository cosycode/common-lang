package com.github.cosycode.common.util.reflex;

import com.github.cosycode.common.lang.ActionExecException;
import com.github.cosycode.common.lang.ShouldNotHappenException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * <b>Description : </b> 反射工具类
 * <p>
 * <b>created in </b> 2021/3/8
 *
 * @author CPF
 * @since 1.2
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflexUtils {

    /**
     * 通过反射从 对象中 获取属性值
     *
     * @param obj           带获取值的对象
     * @param attributeName 属性名称
     * @return 对象中对应属性的值
     * @throws NoSuchFieldException 对象中没有对应属性
     */
    @SuppressWarnings("java:S3011")
    public static Object getAttributeFromObject(@NonNull Object obj, @NonNull String attributeName) throws NoSuchFieldException {
        final Class<?> aClass = obj.getClass();
        final Field declaredField = aClass.getDeclaredField(attributeName);
        try {
            declaredField.setAccessible(true);
            return declaredField.get(obj);
        } catch (IllegalAccessException e) {
            throw new ShouldNotHappenException(e);
        }
    }

    /**
     * 创建一个实例
     *
     * @param cls 待创建的cls对象
     * @param initArgs 参数
     * @return 创建的实例
     * @param <T> 新建类的类型
     */
    public static <T> T newInstance(@NonNull Class<T> cls, Object... initArgs) {
        T instance;
        try {
            if (initArgs == null) {
                instance = cls.getDeclaredConstructor().newInstance();
            } else {
                instance = cls.getDeclaredConstructor().newInstance(initArgs);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ActionExecException("new Instance failure", e);
        }
        return instance;
    }

}
