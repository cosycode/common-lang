package com.github.cosycode.common.ext.mapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>Description : </b> 静态字典池, 存放代码中原本存在的字典内容
 * <p>
 * <b>created in </b> 2019/12/13
 *
 * @author CPF
 * @since 1.0
 **/
class MappingBeanPool {

    private MappingBeanPool() {
    }

    /**
     * 用于存储字典数据
     */
    private static final Map<IMapping<?, ?>, MappingBean<?, ?>> DICT_ITEM_MAP = new ConcurrentHashMap<>();

    /**
     * 往 map 中添加代码项
     */
    public static <K, V> void put(IMapping<K, V> iCodeItem, K key, V val) {
        DICT_ITEM_MAP.put(iCodeItem, new MappingBean<>(key, val));
    }

    /**
     * 获取静态数据
     */
    @SuppressWarnings("unchecked")
    public static <K, V> MappingBean<K, V> get(IMapping<K, V> iDictItem) {
        return (MappingBean<K, V>) DICT_ITEM_MAP.get(iDictItem);
    }

}
