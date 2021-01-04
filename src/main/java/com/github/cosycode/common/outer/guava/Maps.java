package com.github.cosycode.common.outer.guava;

import com.github.cosycode.common.validate.RequireUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>Description : </b> copy from guava:Maps
 *
 * @author CPF
 * @since 1.0
 */
public class Maps {

    private Maps() {
    }

    /**
     * The largest power of two that can be represented as an {@code int}.
     *
     * @since 10.0
     */
    public static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    /**
     * Returns a capacity that is sufficient to keep the map from being resized as
     * long as it grows no larger than expectedSize and the load factor is >= its
     * default (0.75).
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
        return Integer.MAX_VALUE; // any large value
    }

    /**
     * Creates a {@code HashMap} instance, with a high enough "initial capacity"
     * that it <i>should</i> hold {@code expectedSize} elements without growth.
     * This behavior cannot be broadly guaranteed, but it is observed to be true
     * for OpenJDK 1.7. It also can't be guaranteed that the method isn't
     * inadvertently <i>oversizing</i> the returned map.
     *
     * @param expectedSize the number of entries you expect to add to the
     *                     returned map
     * @param <K>          key的类型
     * @param <V>          value的类型
     * @return a new, empty {@code HashMap} with enough capacity to hold {@code
     * expectedSize} entries without resizing
     * @throws IllegalArgumentException if {@code expectedSize} is negative
     */
    public static <K, V> Map<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap<>(capacity(expectedSize));
    }

}
