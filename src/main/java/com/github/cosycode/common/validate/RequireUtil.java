package com.github.cosycode.common.validate;

import com.github.cosycode.common.lang.CheckException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * <b>Description : </b> 用于对一些对象进行要求判断, 如果不满足要求, 则抛出异常
 * <p>
 * <b>created in </b> 2019/4/12
 *
 * @author CPF
 * @since 1.0
 */
public class RequireUtil {

    private RequireUtil() {
    }

    /**
     * 为空则抛出异常
     *
     * @param str 需要验证的字符串
     */
    public static void requireStringNonBlank(String str) {
        if (str == null || str.length() == 0) {
            throw new CheckException("string cannot be null or empty!");
        }
    }

    /**
     * 为空则抛出异常
     *
     * @param strs 需要验证的字符串数组
     */
    public static void requireStringNonBlank(String... strs) {
        for (String str : strs) {
            requireStringNonBlank(str);
        }
    }

    /**
     * 为空则抛出异常
     *
     * @param collection 集合
     * @param <T>        集合类型
     */
    public static <T> void requireCollectNonBlank(Collection<T> collection) {
        Objects.requireNonNull(collection);
        if (collection.isEmpty()) {
            throw new CheckException("the collection is empty!");
        }
    }

    /**
     * 为空则抛出异常
     *
     * @param map map类型
     */
    public static void requireMapNonBlank(Map<?, ?> map) {
        Objects.requireNonNull(map);
        if (map.isEmpty()) {
            throw new CheckException("map is empty!");
        }
    }

    /**
     * flag不为true则抛出异常
     *
     * @param flag 待验证的值
     */
    public static void requireBooleanTrue(boolean flag) {
        if (!flag) {
            throw new CheckException("boolean val is false");
        }
    }

    /**
     * flag不为true则抛出异常
     *
     * @param flag    待验证的值
     * @param message 如果 flag 为 false 时抛出的异常信息
     */
    public static void requireBooleanTrue(boolean flag, String message) {
        if (!flag) {
            throw new CheckException("false: " + message);
        }
    }

    /**
     * arrs 中不包含 target 则抛出异常
     *
     * @param target 目标
     * @param arrs   数组
     */
    public static void requireContainsTargetInArrays(Object target, Object... arrs) {
        if (!Arrays.asList(arrs).contains(target)) {
            throw new CheckException("array does not contain target");
        }
    }
}
