package com.github.cosycode.common.ext.hub;

import com.github.cosycode.common.base.ConsumerWithThrow;
import com.github.cosycode.common.base.FunctionWithThrow;
import com.github.cosycode.common.base.RunnableWithThrow;
import com.github.cosycode.common.base.SupplierWithThrow;
import com.github.cosycode.common.lang.RuntimeExtException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Consumer;

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
public class Promise {

    /**
     * <b>Description : </b> 异常封装处理Bean, 用于没有返回值的异常封装
     * <p>
     * <p> 类中使用了链式调用, 其中的很多方法在执行之后都会返回其自身
     * <b>created in </b> 2021/3/30
     *
     * @author CPF
     * @since 1.3
     **/
    public static class PromiseForRunnable<E extends Exception> {

        @Getter
        protected Exception throwable;

        /**
         * 如果是 private的话, 子类之外无法调用该方法
         */
        void setThrowable(Exception throwable) {
            this.throwable = throwable;
        }

        /**
         * 如果有异常, 则对异常进行处理
         * <p>该方法中 E 不应该是runtimeException中的子类, 由于java语言特性, 在此很难直接通过语法进行限制.</p>
         * <p>但是我觉得, 应该不会有人在方法上直接声明 throw RuntimeException 及其子类吧 </p>
         *
         * @param throwableConsumer 异常处理方法
         * @return 链式调用, 返回对象本身
         */
        public PromiseForRunnable<E> catCh(Consumer<E> throwableConsumer) {
            if (this.throwable != null) {
                if (this.throwable instanceof RuntimeException) {
                    log.debug("throwable: {} 不应该是 RuntimeException 的子类, 建议不要声明 throws RuntimeException 及其子类", this.throwable.getClass());
                } else {
                    throwableConsumer.accept((E) this.throwable);
                }
            }
            return this;
        }

        /**
         * 对指定异常进行捕获
         *
         * @param throwableConsumer 异常处理方法
         * @return 链式调用, 返回对象本身
         */
        public <T extends E> PromiseForRunnable<E> catCh(@NonNull Class<T> catchEptType, Consumer<T> throwableConsumer) {
            if (catchEptType.isInstance(this.throwable)) {
                throwableConsumer.accept((T) this.throwable);
            }
            return this;
        }

        /**
         * 如果有异常, 则对异常进行处理
         *
         * @param throwableConsumer 异常处理方法
         * @return 链式调用, 返回对象本身
         */
        public PromiseForRunnable<E> catchAll(Consumer<Exception> throwableConsumer) {
            if (this.throwable != null) {
                throwableConsumer.accept(this.throwable);
            }
            return this;
        }

        /**
         * 使用JDK自带函数打印异常堆栈
         */
        public PromiseForRunnable<E> catchToPrintStackTrace() {
            if (this.throwable != null) {
                this.throwable.printStackTrace();
            }
            return this;
        }

        /**
         * 如果有异常则打印异常信息
         */
        public PromiseForRunnable<E> logThrowable() {
            if (this.throwable != null) {
                log.error("", this.throwable);
            }
            return this;
        }

        /**
         * 如果有异常则打印异常信息
         *
         * @param message 异常标记文本
         */
        public PromiseForRunnable<E> logThrowable(String message) {
            if (this.throwable != null) {
                log.error(message, this.throwable);
            }
            return this;
        }

        /**
         * 将非运行时异常封装到运行时异常并抛出
         */
        public PromiseForRunnable<E> runtimeExp() {
            if (this.throwable != null) {
                throw new RuntimeExtException(this.throwable);
            }
            return this;
        }

        /**
         * 将非运行时异常封装到运行时异常并抛出
         *
         * @param message 异常文本
         */
        public PromiseForRunnable<E> runtimeExp(String message) {
            if (this.throwable != null) {
                throw new RuntimeExtException(message, this.throwable);
            }
            return this;
        }

        /**
         * 如果执行成功, 则执行 runnable
         *
         * @param runnable 执行函数
         */
        public PromiseForRunnable<E> then(Runnable runnable) {
            if (this.throwable == null) {
                runnable.run();
            }
            return this;
        }

    }

    /**
     * <b>Description : </b> 异常封装处理Bean, 用于有`返回值的异常封装
     * <p>
     * <p> 类中使用了链式调用, 其中的很多方法在执行之后都会返回其自身
     * <b>created in </b> 2021/3/30
     *
     * @author CPF
     * @since 1.3
     **/
    public static class PromiseForSupplier<R, E extends Exception> extends PromiseForRunnable<E> {

        @Getter
        private R returnVal;

        private void setReturnVal(R returnVal) {
            this.returnVal = returnVal;
        }

        public Optional<R> optional() {
            return Optional.ofNullable(returnVal);
        }

        public R getDefaultVal(R defaultVal) {
            return returnVal == null ? defaultVal : returnVal;
        }

        /**
         * {@link PromiseForRunnable#catCh(java.util.function.Consumer)}
         */
        @Override
        public PromiseForSupplier<R, E> catCh(Consumer<E> throwableConsumer) {
            super.catCh(throwableConsumer);
            return this;
        }

        /**
         * {@link PromiseForRunnable#catCh(java.lang.Class, java.util.function.Consumer)}
         */
        @Override
        public <T extends E> PromiseForSupplier<R, E> catCh(@NonNull Class<T> catchEptType, Consumer<T> throwableConsumer) {
            super.catCh(catchEptType, throwableConsumer);
            return this;
        }

        /**
         * {@link PromiseForRunnable#catchAll(java.util.function.Consumer)}
         */
        @Override
        public PromiseForSupplier<R, E> catchAll(Consumer<Exception> throwableConsumer) {
            super.catchAll(throwableConsumer);
            return this;
        }

        /**
         * {@link PromiseForRunnable#catchToPrintStackTrace()}
         */
        @Override
        public PromiseForSupplier<R, E> catchToPrintStackTrace() {
            super.catchToPrintStackTrace();
            return this;
        }

        /**
         * {@link PromiseForRunnable#logThrowable()}
         */
        @Override
        public PromiseForSupplier<R, E> logThrowable() {
            if (this.throwable != null) {
                log.error("", this.throwable);
            }
            return this;
        }

        /**
         * {@link PromiseForRunnable#logThrowable(java.lang.String)}
         */
        @Override
        public PromiseForSupplier<R, E> logThrowable(String message) {
            if (this.throwable != null) {
                log.error(message, this.throwable);
            }
            return this;
        }

        /**
         * {@link PromiseForRunnable#runtimeExp()}
         */
        @Override
        public PromiseForSupplier<R, E> runtimeExp() {
            if (this.throwable != null) {
                throw new RuntimeExtException(this.throwable);
            }
            return this;
        }

        /**
         * {@link PromiseForRunnable#runtimeExp(java.lang.String)}
         */
        @Override
        public PromiseForSupplier<R, E> runtimeExp(String message) {
            if (this.throwable != null) {
                throw new RuntimeExtException(message, this.throwable);
            }
            return this;
        }

        /**
         * 如果执行成功, 则执行 runnable
         *
         * @param runnable 执行函数
         */
        @Override
        public PromiseForSupplier<R, E> then(Runnable runnable) {
            if (this.throwable == null) {
                runnable.run();
            }
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
    public static <E extends Exception> PromiseForRunnable<E> run(RunnableWithThrow<E> runnable) {
        PromiseForRunnable<E> promise = new PromiseForRunnable<>();
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
    public static <P, E extends Exception> PromiseForRunnable<E> consume(P p, ConsumerWithThrow<P, E> consumer) {
        PromiseForRunnable<E> promise = new PromiseForRunnable<>();
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
    public static <R, E extends Exception> PromiseForSupplier<R, E> sup(SupplierWithThrow<R, E> supplier) {
        PromiseForSupplier<R, E> promise = new PromiseForSupplier<>();
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
    public static <P, R, E extends Exception> PromiseForSupplier<R, E> fun(P p, FunctionWithThrow<P, R, E> function) {
        PromiseForSupplier<R, E> promise = new PromiseForSupplier<>();
        R r = null;
        try {
            r = function.apply(p);
        } catch (Exception e) {
            promise.setThrowable(e);
        }
        promise.setReturnVal(r);
        return promise;
    }

}
