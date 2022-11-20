package com.github.cosycode.common.ext.hub;

import com.github.cosycode.common.base.ConsumerWithThrow;
import com.github.cosycode.common.base.FunctionWithThrow;
import com.github.cosycode.common.base.RunnableWithThrow;
import com.github.cosycode.common.base.SupplierWithThrow;
import com.github.cosycode.common.lang.RuntimeExtException;
import com.github.cosycode.common.util.otr.PrintTool;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <b>Description : </b> 异常处理精简类, 该类旨在简化代码结构, 并没有添加太多功能.
 * <p> 一般用于个人项目, 或者是很难出错的语句, 不建议在生产环境使用
 * <b>created in </b> 2021/3/30
 *
 * @author CPF
 * @since 1.0
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class Throws {

    /**
     * <b>Description : </b> 异常封装处理Bean, 用于没有返回值的异常封装
     * <p>
     * 类中使用了链式调用, 其中的很多方法在执行之后都会返回其自身
     * </p>
     * <b>created in </b> 2021/3/30
     *
     * @author CPF
     * @since 1.3
     **/
    public static class PromiseForRunnable {

        @Getter
        protected Exception exception;

        /**
         * 如果是 private的话, 子类之外无法调用该方法
         */
        void setThrowable(Exception exception) {
            this.exception = exception;
        }

        /**
         * 如果有异常, 则对异常进行处理, 对异常处理之后, 则将 exception 设置为 null
         *
         * @param exceptionConsumer 异常处理方法
         * @return 链式调用, 返回对象本身
         */
        public <T> PromiseForRunnable catchEpt(@NonNull Class<T> catchEptType, Consumer<T> exceptionConsumer) {
            Exception ept = this.exception;
            if (ept == null) {
                return this;
            }
            if (catchEptType.isInstance(ept)) {
                exceptionConsumer.accept((T) ept);
            }
            return this;
        }

        /**
         * 如果有异常, 则对异常进行处理
         *
         * @param exceptionConsumer 异常处理方法
         * @return 链式调用, 返回对象本身
         */
        public PromiseForRunnable catchAllEpt(Consumer<Exception> exceptionConsumer) {
            return catchEpt(Exception.class, exceptionConsumer);
        }

        /**
         * 使用JDK自带函数打印异常堆栈
         */
        public PromiseForRunnable logByPrintStackTrace() {
            if (this.exception != null) {
                this.exception.printStackTrace();
            }
            return this;
        }

        /**
         * 如果有异常则打印异常信息
         *
         * @param message 异常标记文本
         */
        public PromiseForRunnable logThrowable(String message) {
            if (this.exception != null) {
                log.error(message, this.exception);
            }
            return this;
        }

        /**
         * 如果有异常则打印异常信息
         */
        public PromiseForRunnable logThrowable() {
            logThrowable("");
            return this;
        }

        /**
         * 将非运行时异常封装到运行时异常并抛出,
         * <p>
         * message 中 {} 的字符串将会被替换成值
         * </p>
         *
         * @param message 异常文本
         */
        public PromiseForRunnable runtimeExp(String message, Object... infos) {
            Exception ept = this.exception;
            if (ept != null) {
                final String msg = infos == null ? message : PrintTool.format(message, infos);
                throw new RuntimeExtException(msg, ept);
            }
            return this;
        }

        /**
         * 将非运行时异常封装到运行时异常并抛出
         */
        public PromiseForRunnable runtimeExp() {
            runtimeExp("");
            return this;
        }
    }

    /**
     * <b>Description : </b> 异常封装处理Bean, 用于有`返回值的异常封装
     * <p>
     * 类中使用了链式调用, 其中的很多方法在执行之后都会返回其自身
     * </p>
     * <b>created in </b> 2021/3/30
     *
     * @author CPF
     * @since 1.3
     **/
    public static class PromiseForSupplier<R> extends PromiseForRunnable {

        private R returnVal;

        private void setReturnVal(R returnVal) {
            this.returnVal = returnVal;
        }

        public R value() {
            return returnVal;
        }

        /**
         * @param eptValue 异常时 value
         * @return 如果发生异常时返回 eptValue, 没有发成异常时返回 value
         */
        public R valueIfEpt(R eptValue) {
            return exception == null ? returnVal : eptValue;
        }

        /**
         * {@link PromiseForRunnable#catchEpt(Class, Consumer)}
         */
        @Override
        public <T> PromiseForSupplier<R> catchEpt(@NonNull Class<T> catchEptType, Consumer<T> exceptionConsumer) {
            super.catchEpt(catchEptType, exceptionConsumer);
            return this;
        }

        /**
         * {@link PromiseForRunnable#catchAllEpt(Consumer)}
         */
        @Override
        public PromiseForSupplier<R> catchAllEpt(Consumer<Exception> exceptionConsumer) {
            return catchEpt(Exception.class, exceptionConsumer);
        }

        /**
         * {@link PromiseForRunnable#logByPrintStackTrace()}
         */
        @Override
        public PromiseForSupplier<R> logByPrintStackTrace() {
            super.logByPrintStackTrace();
            return this;
        }

        /**
         * {@link PromiseForRunnable#logThrowable(String)}
         */
        @Override
        public PromiseForSupplier<R> logThrowable(String message) {
            super.logThrowable(message);
            return this;
        }

        /**
         * {@link PromiseForRunnable#logThrowable()}
         */
        @Override
        public PromiseForSupplier<R> logThrowable() {
            super.logThrowable();
            return this;
        }

        /**
         * {@link PromiseForRunnable#runtimeExp()}
         */
        @Override
        public PromiseForSupplier<R> runtimeExp() {
            super.runtimeExp();
            return this;
        }

        /**
         * {@link PromiseForRunnable#runtimeExp(String, Object...)}
         */
        @Override
        public PromiseForSupplier<R> runtimeExp(String message, Object... infos) {
            super.runtimeExp(message, infos);
            return this;
        }
    }

    /**
     * 对异常进行后续封装处理
     *
     * @param runnable 运行函数
     * @param <E>      需要抛出的异常
     * @return 对函数之后后的异常进行捕获, 并封装成类
     */
    public static <E extends Exception> PromiseForRunnable run(RunnableWithThrow<E> runnable) {
        PromiseForRunnable promise = new PromiseForRunnable();
        try {
            runnable.run();
        } catch (Exception e) {
            promise.setThrowable(e);
        }
        return promise;
    }

    /**
     * 对异常进行后续封装处理
     *
     * @param p        运行函数参数
     * @param consumer 运行函数
     * @param <P>      函数接口参数类型
     * @param <E>      需要抛出的异常
     * @return 对函数之后后的异常进行捕获, 并封装成类
     */
    public static <P, E extends Exception> PromiseForRunnable con(P p, ConsumerWithThrow<P, E> consumer) {
        PromiseForRunnable promise = new PromiseForRunnable();
        try {
            consumer.accept(p);
        } catch (Exception e) {
            promise.setThrowable(e);
        }
        return promise;
    }

    /**
     * @param supplier 运行函数
     * @param <R>      返回值类型
     * @param <E>      需要抛出的异常
     * @return 对函数之后后的异常进行捕获, 并封装成类
     */
    public static <R, E extends Exception> PromiseForSupplier<R> sup(SupplierWithThrow<R, E> supplier) {
        PromiseForSupplier<R> promise = new PromiseForSupplier<>();
        R r = null;
        try {
            r = supplier.get();
        } catch (Exception e) {
            promise.setThrowable(e);
        }
        promise.setReturnVal(r);
        return promise;
    }

    /**
     * @param p        运行函数参数
     * @param function 运行函数
     * @param <P>      函数接口参数类型
     * @param <R>      返回值类型
     * @param <E>      需要抛出的异常
     * @return 对函数之后后的异常进行捕获, 并封装成类
     */
    public static <P, R, E extends Exception> PromiseForSupplier<R> fun(P p, FunctionWithThrow<P, R, E> function) {
        PromiseForSupplier<R> promise = new PromiseForSupplier<>();
        R r = null;
        try {
            r = function.apply(p);
        } catch (Exception e) {
            promise.setThrowable(e);
        }
        promise.setReturnVal(r);
        return promise;
    }

    /**
     * 忽略空指针异常, 存在空指针, 则直接返回 eptValue
     * <p>
     *     使用于多层方法调用获取值的场景,
     *     如下示例多级 get, 每一步都可能有空指针, 导致每一步都需要判空,
     *     实际上只要发生空指针异常, 就可以终止了.
     *     <br>
     *     case:
     *     o1.getO2().getO3().getO4().
     * </p>
     *
     * @param supplier 运行函数
     * @param eptValue 存在空指针时候的返回值
     * @param <R>      返回值类型
     * @return 存在空指针, 则直接返回 null, 没有空指针, 则返回 supplier 返回值
     */
    public static <R> R ignoreNpt(Supplier<R> supplier, R eptValue) {
        try {
            return supplier.get();
        } catch (NullPointerException e) {
            return eptValue;
        }
    }

    /**
     * 忽略空指针异常, 存在空指针, 则直接返回 null
     *
     * @param supplier 运行函数
     * @return 存在空指针, 则直接返回 null, 没有空指针, 则返回 supplier 返回值
     */
    public static <R> R ignoreNpt(Supplier<R> supplier) {
        try {
            return supplier.get();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
