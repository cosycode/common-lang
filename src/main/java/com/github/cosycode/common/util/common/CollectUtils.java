package com.github.cosycode.common.util.common;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Supplier;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2020/6/15 16:26
 */
@Slf4j
public class CollectUtils {

    public <T> T getLastList(List<T> list) {
        return getLastList(list, null);
    }

    public <T> T getFirstList(List<T> list) {
        return getFirstList(list, null);
    }

    public <T> T getFirstList(List<T> list, Supplier<T> supplier) {
        if (list == null || list.isEmpty()) {
            return supplier == null ? null : supplier.get();
        }
        return list.get(0);
    }

    public <T> T getLastList(List<T> list, Supplier<T> supplier) {
        if (list == null || list.isEmpty()) {
            return supplier == null ? null : supplier.get();
        }
        return list.get(list.size() - 1);
    }

}
