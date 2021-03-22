package com.github.cosycode.common.ext.bean;

import com.github.cosycode.common.base.IMapGetter;

import java.util.HashMap;

/**
 * <b>Description : </b> 记录, 继承于HashMap, 添加了增强方法
 * <p>
 * <b>created in </b> 2019/4/3
 *
 * @author CPF
 * @since 1.0
 **/
public class Record extends HashMap<String, Object> implements IMapGetter<String, Object> {

    public void set(String key, Object value) {
        super.put(key, value);
    }

}
