package com.github.cosycode.common.ext.hub;

import com.github.cosycode.common.base.ConsumerWithThrow;
import com.github.cosycode.common.base.RunnableWithThrow;
import com.github.cosycode.common.base.SupplierWithThrow;
import com.github.cosycode.common.lang.ActionExecException;
import lombok.extern.slf4j.Slf4j;

/**
 * <b>Description : </b> 简化代码类, 对冗余的代码进行简化, 增强可读性
 * <p>
 * ignoreException开头, 忽略发生的异常, 仅仅打印日志.
 * <p>
 * runtimeException开头, 原来需要catch的运行时转换为runtimeException抛出
 * <p>
 * <b>created in </b> 2020/6/24
 *
 * @author CPF
 * @since 1.0
 */
@Slf4j
public class SimpleCode {

    private SimpleCode() {
    }


    /**
     * 忽略运行的异常
     *
     * @param runnable 带有异常的运行接口
     * @param message  发生异常报错信息
     * @see SimpleCode#simpleExceptionForRun(RunnableWithThrow, java.lang.String, boolean)
     */
    public static void ignoreException(RunnableWithThrow<? extends Exception> runnable, String message) {
        simpleExceptionForRun(runnable, message, false);
    }

    /**
     * 忽略运行的异常
     *
     * @param runnable 带有异常的运行接口
     * @see SimpleCode#simpleExceptionForRun(RunnableWithThrow, java.lang.String, boolean)
     */
    public static void ignoreException(RunnableWithThrow<? extends Exception> runnable) {
        simpleExceptionForRun(runnable, null, false);
    }

    /**
     * 异常转换为 ActionExecException
     *
     * @param runnable 带有异常的运行接口
     * @param message  发生异常报错信息
     * @see SimpleCode#simpleExceptionForRun(RunnableWithThrow, java.lang.String, boolean)
     */
    public static void runtimeException(RunnableWithThrow<? extends Exception> runnable, String message) {
        simpleExceptionForRun(runnable, message, true);
    }

    /**
     * 异常转换为 ActionExecException
     *
     * @param runnable 带有异常的运行接口
     * @see SimpleCode#simpleExceptionForRun(RunnableWithThrow, java.lang.String, boolean)
     */
    public static void runtimeException(RunnableWithThrow<? extends Exception> runnable) {
        simpleExceptionForRun(runnable, null, true);
    }

    /**
     * 忽略运行的异常
     *
     * @param supplier 带有返回值和throw的函数接口
     * @return supplier 的返回值
     * @see SimpleCode#simpleExceptionForSup(SupplierWithThrow, java.lang.Object, java.lang.String, boolean)
     */
    public static <T> T ignoreException(SupplierWithThrow<T, ? extends Exception> supplier) {
        return simpleExceptionForSup(supplier, null, null, false);
    }

    /**
     * 忽略运行的异常
     *
     * @param supplier     带有返回值和throw的函数接口
     * @param defaultValue supplier 发生错误后的默认返回值
     * @return supplier 的返回值
     * @see SimpleCode#simpleExceptionForSup(SupplierWithThrow, java.lang.Object, java.lang.String, boolean)
     */
    public static <T> T ignoreException(SupplierWithThrow<T, ? extends Exception> supplier, T defaultValue) {
        return simpleExceptionForSup(supplier, defaultValue, null, false);
    }

    /**
     * 忽略运行的异常
     *
     * @param supplier     带有返回值和throw的函数接口
     * @param defaultValue supplier 发生错误后的默认返回值
     * @return supplier 的返回值
     * @see SimpleCode#simpleExceptionForSup(SupplierWithThrow, java.lang.Object, java.lang.String, boolean)
     */
    public static <T> T ignoreException(SupplierWithThrow<T, ? extends Exception> supplier, T defaultValue, String message) {
        return simpleExceptionForSup(supplier, defaultValue, message, false);
    }

    /**
     * 忽略运行的异常
     *
     * @param supplier 带有返回值和throw的函数接口
     * @return supplier 的返回值
     * @see SimpleCode#simpleExceptionForSup(SupplierWithThrow, java.lang.Object, java.lang.String, boolean)
     */
    public static <T> T runtimeException(SupplierWithThrow<T, ? extends Exception> supplier) {
        return simpleExceptionForSup(supplier, null, null, true);
    }

    /**
     * 原来需要 catch 的运行时转换为 runtimeException 抛出
     *
     * @param supplier 带有返回值和throw的函数接口
     * @param message  supplier 发生错误后的信息
     * @return supplier 的返回值
     * @see SimpleCode#simpleExceptionForSup(SupplierWithThrow, java.lang.Object, java.lang.String, boolean)
     */
    public static <T> T runtimeException(SupplierWithThrow<T, ? extends Exception> supplier, String message) {
        return simpleExceptionForSup(supplier, null, message, true);
    }

    /**
     * 忽略运行的异常
     *
     * @param runnable       指定的函数接口
     * @param message        runnable 发生错误后的信息
     * @param throwException true: runnable 发生错误后抛出运行时异常, false: 仅仅打印日志
     */
    public static void simpleExceptionFor(RunnableWithThrow<? extends Exception> runnable, String message, boolean throwException) {
        simpleExceptionForRun(runnable, message, throwException);
    }

    /**
     * 忽略运行的异常
     *
     * @param supplier       带有返回值和throw的函数接口
     * @param defaultValue   throwException为true时, supplier 发生错误后的默认返回值
     * @param message        supplier 发生错误后的信息
     * @param throwException true: supplier 发生错误后抛出运行时异常, false: 仅仅打印日志
     * @param <T>            返回值类型
     * @return supplier 执行成功: supplier的返回值, supplier 执行失败: 返回 defaultValue
     */
    public static <T> T simpleException(SupplierWithThrow<T, ? extends Exception> supplier, T defaultValue, String message, boolean throwException) {
        return simpleExceptionForSup(supplier, defaultValue, message, true, throwException);
    }

    /**
     * 忽略运行的异常
     *
     * @param supplier       带有返回值和throw的函数接口
     * @param defaultValue   throwException为true时, supplier 发生错误后的默认返回值
     * @param message        supplier 发生错误后的信息
     * @param logException   输出异常
     * @param throwException true: supplier 发生错误后抛出运行时异常, false: 仅仅打印日志
     * @param <T>            返回值类型
     * @return supplier 执行成功: supplier的返回值, supplier 执行失败: 返回 defaultValue
     */
    public static <T> T simpleException(SupplierWithThrow<T, ? extends Exception> supplier, T defaultValue, String message, boolean logException, boolean throwException) {
        return simpleExceptionForSup(supplier, defaultValue, message, logException, throwException);
    }


    /**
     * 忽略运行的异常
     *
     * @param runnable 带有异常的运行接口
     * @param message  发生异常报错信息
     * @see SimpleCode#simpleExceptionForRun(RunnableWithThrow, java.lang.String, boolean)
     */
    public static void ignoreExceptionForRun(RunnableWithThrow<? extends Exception> runnable, String message) {
        simpleExceptionForRun(runnable, message, false);
    }

    /**
     * 忽略运行的异常
     *
     * @param runnable 带有异常的运行接口
     * @see SimpleCode#simpleExceptionForRun(RunnableWithThrow, java.lang.String, boolean)
     */
    public static void ignoreExceptionForRun(RunnableWithThrow<? extends Exception> runnable) {
        simpleExceptionForRun(runnable, null, false);
    }

    /**
     * 异常转换为 ActionExecException
     *
     * @param runnable 带有异常的运行接口
     * @param message  发生异常报错信息
     * @see SimpleCode#simpleExceptionForRun(RunnableWithThrow, java.lang.String, boolean)
     */
    public static void runtimeExceptionForRun(RunnableWithThrow<? extends Exception> runnable, String message) {
        simpleExceptionForRun(runnable, message, true);
    }

    /**
     * 异常转换为 ActionExecException
     *
     * @param runnable 带有异常的运行接口
     * @see SimpleCode#simpleExceptionForRun(RunnableWithThrow, java.lang.String, boolean)
     */
    public static void runtimeExceptionForRun(RunnableWithThrow<? extends Exception> runnable) {
        simpleExceptionForRun(runnable, null, true);
    }

    /**
     * 忽略运行的异常
     *
     * @param supplier 带有返回值和throw的函数接口
     * @return supplier 的返回值
     * @see SimpleCode#simpleExceptionForSup(SupplierWithThrow, java.lang.Object, java.lang.String, boolean)
     */
    public static <T> T ignoreExceptionForSup(SupplierWithThrow<T, ? extends Exception> supplier) {
        return simpleExceptionForSup(supplier, null, null, false);
    }

    /**
     * 忽略运行的异常
     *
     * @param supplier     带有返回值和throw的函数接口
     * @param defaultValue supplier 发生错误后的默认返回值
     * @return supplier 的返回值
     * @see SimpleCode#simpleExceptionForSup(SupplierWithThrow, java.lang.Object, java.lang.String, boolean)
     */
    public static <T> T ignoreExceptionForSup(SupplierWithThrow<T, ? extends Exception> supplier, T defaultValue) {
        return simpleExceptionForSup(supplier, defaultValue, null, false);
    }

    /**
     * 忽略运行的异常
     *
     * @param supplier     带有返回值和throw的函数接口
     * @param defaultValue supplier 发生错误后的默认返回值
     * @return supplier 的返回值
     * @see SimpleCode#simpleExceptionForSup(SupplierWithThrow, java.lang.Object, java.lang.String, boolean)
     */
    public static <T> T ignoreExceptionForSup(SupplierWithThrow<T, ? extends Exception> supplier, T defaultValue, String message) {
        return simpleExceptionForSup(supplier, defaultValue, message, false);
    }

    /**
     * 忽略运行的异常
     *
     * @param supplier 带有返回值和throw的函数接口
     * @return supplier 的返回值
     * @see SimpleCode#simpleExceptionForSup(SupplierWithThrow, java.lang.Object, java.lang.String, boolean)
     */
    public static <T> T runtimeExceptionForSup(SupplierWithThrow<T, ? extends Exception> supplier) {
        return simpleExceptionForSup(supplier, null, null, true);
    }

    /**
     * 原来需要 catch 的运行时转换为 runtimeException 抛出
     *
     * @param supplier 带有返回值和throw的函数接口
     * @param message  supplier 发生错误后的信息
     * @return supplier 的返回值
     * @see SimpleCode#simpleExceptionForSup(SupplierWithThrow, java.lang.Object, java.lang.String, boolean)
     */
    public static <T> T runtimeExceptionForSup(SupplierWithThrow<T, ? extends Exception> supplier, String message) {
        return simpleExceptionForSup(supplier, null, message, true);
    }

    /**
     * 忽略运行的异常
     *
     * @param runnable       指定的函数接口
     * @param message        runnable 发生错误后的信息
     * @param throwException true: runnable 发生错误后抛出运行时异常, false: 仅仅打印日志
     */
    public static void simpleExceptionForRun(RunnableWithThrow<? extends Exception> runnable, String message, boolean throwException) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (message == null) {
                message = "simpleException";
            }
            if (throwException) {
                throw new ActionExecException(message, e);
            } else {
                log.error(message, e);
            }
        }
    }

    /**
     * 忽略运行的异常
     *
     * @param supplier       带有返回值和throw的函数接口
     * @param defaultValue   throwException为true时, supplier 发生错误后的默认返回值
     * @param message        supplier 发生错误后的信息
     * @param throwException true: supplier 发生错误后抛出运行时异常, false: 仅仅打印日志
     * @param <T>            返回值类型
     * @return supplier 执行成功: supplier的返回值, supplier 执行失败: 返回 defaultValue
     */
    public static <T> T simpleExceptionForSup(SupplierWithThrow<T, ? extends Exception> supplier, T defaultValue, String message, boolean throwException) {
        return simpleExceptionForSup(supplier, defaultValue, message, true, throwException);
    }

    /**
     * 忽略运行的异常
     *
     * @param supplier       带有返回值和throw的函数接口
     * @param defaultValue   throwException为true时, supplier 发生错误后的默认返回值
     * @param message        supplier 发生错误后的信息
     * @param logException   输出异常
     * @param throwException true: supplier 发生错误后抛出运行时异常, false: 仅仅打印日志
     * @param <T>            返回值类型
     * @return supplier 执行成功: supplier的返回值, supplier 执行失败: 返回 defaultValue
     */
    public static <T> T simpleExceptionForSup(SupplierWithThrow<T, ? extends Exception> supplier, T defaultValue, String message, boolean logException, boolean throwException) {
        try {
            return supplier.get();
        } catch (Exception e) {
            if (logException) {
                log.warn(message, e);
            } else {
                log.warn(message);
            }
            if (throwException) {
                throw new ActionExecException(message, e);
            }
        }
        return defaultValue;
    }

    /**
     * @param runnableWithThrow 函数表达式(带有异常)
     * @return 若出现异常, 则将异常返回, 否则返回 null
     */
    public static Exception catchThrow(RunnableWithThrow<? extends Exception> runnableWithThrow) {
        try {
            runnableWithThrow.run();
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    /**
     * @param consumerWithThrow 函数表达式(带有异常)
     * @return 若出现异常, 则将异常返回, 否则返回 null
     */
    public static <T> Exception catchThrow(ConsumerWithThrow<T, ? extends Exception> consumerWithThrow, T t) {
        try {
            consumerWithThrow.accept(t);
            return null;
        } catch (Exception e) {
            return e;
        }
    }

}
