package com.github.cosycode.common.util.common;

import com.github.cosycode.common.lang.WrongBranchException;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <b>Description : </b> 基础类和常用类的类型转换
 * <p>
 * <b>created in </b> 2021/1/27
 *
 * @author CPF
 * @since 1.0
 **/
public class TypeConvertUtils {

    private TypeConvertUtils() {
    }

    /**
     * 将 s 转换为 tClass 类型
     *
     * @param s      数据源对象
     * @param tClass 转换成的目标对象类型
     * @param <S>    数据源对象类型
     * @param <T>    目标数据类型
     * @return 若可以转换, 则返回 s 转换为 tClass 类型后的对象, 若无法转换则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <S, T> T convert(@NonNull S s, @NonNull Class<T> tClass) {
        // 如果类型相同, 则直接返回
        if (s.getClass().equals(tClass)) {
            return (T) s;
        }
        if (s instanceof String) {
            return convertFromString((String) s, tClass);
        }
        // 如果两种类型分别是基本类型和包装类型的话可以直接转换; eg: int.class 和 Integer.class
        if (getPackageType(tClass).isAssignableFrom(getPackageType(s.getClass()))) {
            return (T) s;
        }
        if (tClass == Integer.class || tClass == int.class) {
            return (T) convertToInteger(s);
        }
        return null;
    }

    /**
     * 将字符串 s 转换为 tClass 类型
     *
     * @param s     待转换的字符串
     * @param clazz 转换成的目标对象类型
     * @param <T>   目标数据类型
     * @return 若可以转换, 则返回 s 转换为 tClass 类型后的对象, 若无法转换则返回 null
     */
    @SuppressWarnings("unchecked")
    private static <T> T convertFromString(@NonNull String s, Class<T> clazz) {
        if (clazz == Integer.class || clazz == Integer.TYPE) {
            return (T) Integer.valueOf(s);
        }
        if (clazz == Boolean.class || clazz == Boolean.TYPE) {
            return (T) Boolean.valueOf(s);
        }
        if (clazz == Short.class || clazz == Short.TYPE) {
            return (T) Short.valueOf(s);
        }
        if (clazz == Long.class || clazz == Long.TYPE) {
            return (T) Long.valueOf(s);
        }
        if (clazz == BigDecimal.class) {
            return (T) new BigDecimal(s);
        }
        if (clazz == BigInteger.class) {
            return (T) new BigInteger(s);
        }
        if (clazz == Double.class || clazz == Double.TYPE) {
            return (T) Double.valueOf(s);
        }
        if (clazz == Float.class || clazz == Float.TYPE) {
            return (T) Float.valueOf(s);
        }
        if (clazz == Byte.class || clazz == Byte.TYPE) {
            return (T) Byte.valueOf(s);
        }
        return null;
    }


    private static <S> Integer convertToInteger(@NonNull S s) {
        if (s instanceof String) {
            return Integer.parseInt(s.toString());
        }
        return null;
    }

    /**
     * 通过基本数据类型获取包装类型, 如果传入的不是基本数据类型的 class 的话则返回其本身
     * <p>
     * <b>example : </b> 传入 int.class 返回 Integer.class
     * <p>
     * <b>example : </b> 传入 Integer.class 返回 Integer.class
     *
     * @param clazz 基本数据类型
     * @return 包装类型
     */
    public static Class<?> getPackageType(Class<?> clazz) {
        if (!clazz.isPrimitive()) {
            return clazz;
        }
        if (byte.class.equals(clazz)) {
            return Byte.class;
        } else if (short.class.equals(clazz)) {
            return Short.class;
        } else if (int.class.equals(clazz)) {
            return Integer.class;
        } else if (long.class.equals(clazz)) {
            return Long.class;
        } else if (float.class.equals(clazz)) {
            return Float.class;
        } else if (double.class.equals(clazz)) {
            return Double.class;
        } else if (char.class.equals(clazz)) {
            return Character.class;
        } else if (boolean.class.equals(clazz)) {
            return Boolean.class;
        } else {
            throw new WrongBranchException("class: " + clazz.getName() + " 不是包装类型");
        }
    }

}
