package com.github.cosycode.common.ext.bean;

import com.github.cosycode.common.base.IMapGetter;

import java.util.HashMap;

/**
 * <b>Description : </b> 小写型记录, 继承于HashMap, 在插入时key自动转换为小写形式
 * <p>
 * <b>created in </b> 2019/4/3
 *
 * @author CPF
 * @see Record
 * @since 1.0
 **/
public class LowerRecord extends HashMap<String, Object> implements IMapGetter<String, Object> {

    @Override
    public Object put(String key, Object value) {
        if (key == null || "".equals(key.trim())) {
            return "";
        }
        return super.put(key.toLowerCase(), value);
    }

    public void simplePut(String key, String value) {
        super.put(key, value);
    }

}
