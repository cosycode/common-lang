package com.github.cosycode.common.util.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Supplier;

/**
 * <b>Description : </b> 集合工具类
 * <p>
 * <b>created in </b> 2020/6/15
 *
 * @author CPF
 * @since 1.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectUtils {

    public static <T> T getLastList(List<T> list) {
        return getLastList(list, null);
    }

    public static <T> T getFirstList(List<T> list) {
        return getFirstList(list, null);
    }

    public static <T> T getFirstList(List<T> list, Supplier<T> supplier) {
        if (list == null || list.isEmpty()) {
            return supplier == null ? null : supplier.get();
        }
        return list.get(0);
    }

    public static <T> T getLastList(List<T> list, Supplier<T> supplier) {
        if (list == null || list.isEmpty()) {
            return supplier == null ? null : supplier.get();
        }
        return list.get(list.size() - 1);
    }

}
