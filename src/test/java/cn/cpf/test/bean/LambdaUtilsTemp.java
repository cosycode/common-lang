package cn.cpf.test.bean;

import lombok.NonNull;

import java.lang.reflect.Method;
import java.util.function.*;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/17
 *
 * @author CPF
 * @since
 **/
public class LambdaUtilsTemp {


    public static Object getLambdaType(@NonNull Object object) {
        final Method[] declaredMethods = object.getClass().getDeclaredMethods();
        final Method method;
        if (declaredMethods.length == 0) {
            return null;
        } else if (declaredMethods.length == 1) {
            method = declaredMethods[0];
        } else {
            throw new IllegalArgumentException("TODO 需要处理多个");
        }
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final int length = parameterTypes.length;
        final Class<?> returnType = method.getReturnType();
        if (void.class.equals(returnType)) {
            if (length == 0) {
                return Runnable.class;
            } else if (length == 1) {
                return Consumer.class;
            } else if (length == 2) {
                return BiConsumer.class;
            } else {
                throw new RuntimeException("fjkd");
            }
        } else {
            if (length == 0) {
                return Supplier.class;
            } else if (length == 1) {
                return Function.class;
            } else if (length == 2) {
                return BiFunction.class;
            } else {
                throw new RuntimeException("fjkfdfddd");
            }
        }
    }

}
