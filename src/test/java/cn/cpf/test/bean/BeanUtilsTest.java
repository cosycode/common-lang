package cn.cpf.test.bean;

import com.github.cosycode.common.base.ConsumerWithThrow;
import com.github.cosycode.common.base.FunctionWithThrow;
import com.github.cosycode.common.base.RunnableWithThrow;
import com.github.cosycode.common.base.SupplierWithThrow;
import lombok.extern.slf4j.Slf4j;

/**
 * <b>Description : </b>
 *
 * @author CPF
 **/
@Slf4j
public class BeanUtilsTest {

    public void test(RunnableWithThrow<?> runnable) {
        System.out.println("RunnableWithThrow");
    }

    public void test(FunctionWithThrow<?,?,?> runnable) {
        System.out.println("FunctionWithThrow");
    }

    public void test(ConsumerWithThrow<?,?> runnable) {
        System.out.println("ConsumerWithThrow");
    }
    public void test(SupplierWithThrow<?,?> runnable) {
        System.out.println("SupplierWithThrow");
    }


    public static void main(String[] args) {
        final BeanUtilsTest beanUtilsTest = new BeanUtilsTest();

        RunnableWithThrow run = () -> System.out.println("RunnableWithThrow");
        SupplierWithThrow sup = () -> {
            System.out.println("SupplierWithThrow");
            return "SupplierWithThrow Result";
        };
        ConsumerWithThrow consumer = (a) -> System.out.println("ConsumerWithThrow\n" + a);
        FunctionWithThrow fun = (a) -> {
            System.out.println("RunnableWithThrow");
            return "FunctionWithThrow Result";
        };
        beanUtilsTest.test(run);
        beanUtilsTest.test(sup);
        beanUtilsTest.test(consumer);
        beanUtilsTest.test(fun);
    }

}
