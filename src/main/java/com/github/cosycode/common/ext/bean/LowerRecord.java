package com.github.cosycode.common.ext.bean;

import com.github.cosycode.common.base.IMapGetter;

import java.util.HashMap;

/**
 * <b>Description : </b> 传入的 Key 值全部会转换为小写形式
 *
 * @author CPF
 * @since 1.0
 * @date 2019/4/3
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
