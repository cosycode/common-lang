package com.github.cosycode.common.util.common;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * <b>Description : </b> Bean集合 工具类
 * <p>
 * <b>created in </b> 2020/6/15
 *
 * @author CPF
 * @since 1.0
 */
@Slf4j
public class BeanListUtils {

    private BeanListUtils() {
    }

    /**
     * 将 list 按照指定规则转为 Map
     *
     * @param getHashKey 从对象中寻求 hash key 的方法
     * @param list    待转换的list
     * @param <K>     hash key
     * @param <V>     hash value
     * @return 转换后的map
     */
    public static <K, V> Map<K, V> castListToMap(@NonNull List<V> list, @NonNull Function<V, K> getHashKey) {
        if (list.isEmpty()) {
            return new HashMap<>();
        }
        Map<K, V> map = Maps.newHashMapWithExpectedSize(list.size());
        list.forEach(it -> {
            if (it == null) {
                return;
            }
            K key = getHashKey.apply(it);
            if (key != null) {
                map.put(key, it);
            }
        });
        return map;
    }

}
