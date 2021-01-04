package com.github.cosycode.common.ext.mapping;

/**
 * <b>Description : </b> 用于存放 Mapping
 * <p>
 * <b>created in </b> 2019/12/13
 *
 * @author CPF
 * @since 1.0
 **/
public class MappingBean<K, V> {

    public MappingBean(K key, V val) {
        this.key = key;
        this.val = val;
    }

    private K key;

    private V val;

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getVal() {
        return val;
    }

    public void setVal(V val) {
        this.val = val;
    }

}
