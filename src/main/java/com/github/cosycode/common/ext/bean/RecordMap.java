package com.github.cosycode.common.ext.bean;

import com.github.cosycode.common.base.IGetter;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>Description : </b> 对Map的组合形式Bean
 * <p>
 * <b>created in </b> 2019/8/1
 *
 * @author CPF
 * @see Record
 * @since 1.0
 **/
public class RecordMap implements IGetter<String> {

    protected Map<String, Object> map = null;

    public Object put(String key, Object object) {
        if (map == null) {
            map = new HashMap<>();
        }
        return map.put(key, object);
    }

    public void putAll(Map<String, Object> objectMap) {
        if (map == null) {
            map = objectMap;
        }
        map.putAll(objectMap);
    }

    @Override
    public Object get(String key) {
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    /**
     * 移除 map 中 key 的键值对应的对象
     *
     * @param key 移除的map中的键
     * @return 移除的对象
     */
    public Object remove(String key) {
        if (map == null) {
            return null;
        }
        return map.remove(key);
    }

    /**
     * 移除 map 中 key 的键值对应的对象, 并转换为指定类型 T 返回
     *
     * @param key    移除的map中的键
     * @param tClass 移除的对象的class类型
     * @param <T>    移除对象的类型
     * @return 移除的对象
     */
    public <T> T remove(String key, Class<T> tClass) {
        Object obj = remove(key);
        return tClass.cast(obj);
    }

    protected Map<String, Object> getMap() {
        return this.map;
    }

}
