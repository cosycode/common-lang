package com.github.cosycode.common.ext.bean;

import com.github.cosycode.common.base.IMapGetter;
import com.github.cosycode.common.util.common.StrUtils;

import java.util.HashMap;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2019/4/23
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
     * 将Key转换为驼峰式
     */
    public Object set(String key, Object value) {
        return super.put(key, value);
    }

}
