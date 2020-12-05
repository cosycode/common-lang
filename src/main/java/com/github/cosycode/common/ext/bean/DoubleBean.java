package com.github.cosycode.common.ext.bean;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2020/7/9
 */
public class DoubleBean<O1, O2> {

    public static <O, T> DoubleBean<O, T> of(O o, T t) {
        return new DoubleBean<>(o, t);
    }

    private O1 o1;
    private O2 o2;

    public DoubleBean(O1 o1, O2 o2) {
        this.o1 = o1;
        this.o2 = o2;
    }

    public O1 getO1() {
        return o1;
    }

    public void setO1(O1 o1) {
        this.o1 = o1;
    }

    public O2 getO2() {
        return o2;
    }

    public void setO2(O2 o2) {
        this.o2 = o2;
    }
}
