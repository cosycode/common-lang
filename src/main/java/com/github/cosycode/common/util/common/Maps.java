package com.github.cosycode.common.util.common;

import com.github.cosycode.common.validate.RequireUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2020/7/29 11:42
 */
public class Maps {

    private Maps(){}

    /**
     * 可以表示为{@code int}的2的最大幂。
     *
     * @since 10.0
     */
    public static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    /**
     * 返回的容量足以使地图不被调整为只要它的增长不超过ExpectedSize并且负载因子 >= 默认值（0.75）。
     */
    static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            RequireUtil.requireBooleanTrue(expectedSize > 0);
            return expectedSize + 1;
        }
        if (expectedSize < MAX_POWER_OF_TWO) {
            // This is the calculation used in JDK8 to resize when a putAll
            // happens; it seems to be the most conservative calculation we
            // can make.  0.75 is the default load factor.
            return (int) ((float) expectedSize / 0.75F + 1.0F);
        }
        return Integer.MAX_VALUE;
    }

    /**
     * 创建一个具有足够“初始容量”的{@code HashMap}实例, 它应<i>应</ i>容纳{@code ExpectedSize}元素而不会增长。
     * 这种行为不能得到广泛保证，但可以观察到是真的, 适用于OpenJDK 1.7。也不能保证该方法不是, 不经意<i>过大</ i>返回的地图。
     */
    public static <K, V> Map<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap<>(capacity(expectedSize));
    }

    /**
     * 将旧key对应的Value赋值到新key对应的Value中
     *
     * @param map map
     * @param oldKey 旧的键值
     * @param newKey 新的键值
     * @param <K> map的Key类型
     * @param <V> map的Value类型
     */
    public static <K, V> void changeKey(Map<K, V> map, K oldKey, K newKey) {
        if (map.containsKey(oldKey)) {
            V v = map.remove(oldKey);
            map.put(newKey, v);
        }
    }

    /**
     * 将旧key对应的Value赋值到新key对应的Value中
     *
     * @param map map
     * @param operator 新key提供值(旧key -> 新key)
     * @param <K> map的Key类型
     * @param <V> map的Value类型
     */
    public static <K, V> void changeKey(Map<K, V> map, UnaryOperator<K> operator) {
        map.forEach((key, value) -> {
            K apply = operator.apply(key);
            map.remove(key);
            map.put(apply, value);
        });
    }

    /**
     *
     *
     * @param map map
     * @param <V> map中的Value类型
     */
    public static <V> void changeKeyToCamel(Map<String, V> map) {
        changeKey(map, StrUtils::lowerCamel);
    }

}
