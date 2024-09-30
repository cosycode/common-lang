package com.github.cosycode.common.util.common;

import com.github.cosycode.common.lang.BaseRuntimeException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    public static <T> T findOneMustOnly(@NonNull List<T> collections, Predicate<T> filter) {
        List<T> collect = collections.stream().filter(filter).collect(Collectors.toList());
        requireSize(collect, 1, 1);
        return collect.get(0);
    }

    public static void requireSize(@NonNull Collection<?> collections, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(String.format("min(%s) can't rather than max(%s)", min, max));
        }
        int size = collections.size();
        if (min == max && size != min) {
            throw new BaseRuntimeException("collections size : %s, not equals %s ==> %s", size, min, logCollection(collections, 10));
        }
        if ((min < max) && (size < min || size > max)) {
            throw new BaseRuntimeException("collections size : %s, not in the range [%s, %s] ==> %s", size, min, max, logCollection(collections, 10));
        }
    }

    public static void requireSize(@NonNull Collection<?> collections, int min, int max, Supplier<String> messageSupplier) {
        if (min > max) {
            throw new IllegalArgumentException(String.format("min(%s) can't rather than max(%s) ==> message: %s", min, max, messageSupplier.get()));
        }
        int size = collections.size();
        if (min == max && size != min) {
            throw new BaseRuntimeException("collections size : %s, not equals %s ==> message: %s ==> %s", size, min, messageSupplier.get(), logCollection(collections, 10));
        }
        if ((min < max) && (size < min || size > max)) {
            throw new BaseRuntimeException("collections size : %s, not in the range [%s, %s] ==> message: %s ==> %s", size, min, max, messageSupplier.get(), logCollection(collections, 10));
        }
    }

    private static String logCollection(Collection<?> collections, int limit) {
        if (collections == null) {
            return "";
        }
        if (collections.size() <= limit) {
            return collections.toString();
        }
        return collections.stream().limit(limit).collect(Collectors.toList()).toString();
    }

    public static <T> T listToOne(List<T> collections) {
        if (collections == null) {
            return null;
        }
        return collections.get(0);
    }

    public static <T> T listToOneWithThrow(List<T> collections) {
        if (collections == null || collections.isEmpty()) {
            return null;
        }
        if (collections.size() == 1) {
            return collections.get(0);
        }
        throw new BaseRuntimeException("list contains multi values ==> %s", collections);
    }

    @Data
    static class MultiRecord<T> {
        int size;
        List<T> list;
        T record;

        public MultiRecord(List<T> list) {
            if (list == null) {
                size = 0;
                return;
            }
            size = list.size();
            if (size > 0) {
                record = list.get(0);
                this.list = list;
            }
        }
    }

}
