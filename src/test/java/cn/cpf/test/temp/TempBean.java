package cn.cpf.test.temp;

import cn.cpf.test.temp.TemplateClazz.*;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @date 2020/12/11 15:46
 **/
public class TempBean {

    static class Bean2<T extends Fruit> {

        public T get() {
            return null;
        }
        public void set(T t) {
        }

    }

    // <Apple> 不能转换为 <? extends Apple>
    public static void main(String[] args) {
        Bean2<Apple> appleBean2 = new Bean2<>();
        final Apple apple = appleBean2.get();
        appleBean2.set(apple);
        appleBean2.set(new BlackApple());
    }

}
