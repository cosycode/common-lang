package com.github.cosycode.common.ext.bean;

import com.github.cosycode.common.base.IGetter;

import java.util.HashMap;
import java.util.Map;

/**
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

    public Object remove(String key) {
        if (map == null) {
            return null;
        }
        return map.remove(key);
    }

    public <T> T remove(String key, Class<T> tClass) {
        if (map == null) {
            return null;
        }
        Object remove = map.remove(key);
        if (remove == null) {
            return null;
        }
        return tClass.cast(remove);
    }

    protected Map<String, Object> getMap() {
        return this.map;
    }

}
