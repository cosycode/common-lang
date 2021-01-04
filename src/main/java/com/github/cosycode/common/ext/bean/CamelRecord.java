package com.github.cosycode.common.ext.bean;

import com.github.cosycode.common.base.IMapGetter;
import com.github.cosycode.common.util.common.StrUtils;

import java.util.HashMap;

/**
 * <b>Description : </b> 小写驼峰式记录, 继承于HashMap, 在插入时key自动转换为小写驼峰式
 * <p>
 * <b>created in </b> 2019/4/23
 *
 * @author CPF
 * @see Record
 * @since 1.0
 **/
public class CamelRecord extends HashMap<String, Object> implements IMapGetter<String, Object> {

    /**
     * 将Key转换为驼峰式
     */
    @Override
    public Object put(String key, Object value) {
        if (key == null || "".equals(key.trim())) {
            return "";
        }
        return super.put(StrUtils.lowerCamel(key), value);
    }

    /**
     * 将Key转换为驼峰式, 并存入Map中
     *
     * @param key   指定值关联的键key
     * @param value 与指定键关联的值
     * @return 见 HashMap 的返回值
     */
    public Object set(String key, Object value) {
        return super.put(key, value);
    }

}
