package com.github.cosycode.common.ext.bean;

import com.github.cosycode.common.base.IMapGetter;

import java.util.HashMap;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2019/4/3
 **/
public class Record extends HashMap<String, Object> implements IMapGetter<String, Object> {

    public void set(String key, String value) {
        super.put(key, value);
    }

}
