package com.github.cosycode.common.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>Description : </b> 用于命令行参数的解析
 *
 * @author CPF
 * @since 1.0
 * @date 2019/12/13 10:43
 **/
public class CommandLineHelper {

    /**
     * 存放无key参数
     */
    private List<String> noKeyParams;
    /**
     * 带key参数
     */
    private Map<String, String> keyParams;

    /**
     * 功能是解析命令行的参数
     *
     * @param args program arguments
     */
    @SuppressWarnings("java:S127")
    private CommandLineHelper(String[] args) {
        for (int i = 0, len = args.length; i < len; i++) {
            final String arg = args[i];
            if ('-' == arg.charAt(0)) {
                final String val = i + 1 >= len ? "" : args[++i];
                getKeyParams().put(arg.replaceFirst("-+", "").toLowerCase(), val);
            } else {
                getNoKeyParams().add(arg);
            }
        }
    }

    /**
     * 解析命令行的参数
     *
     * @param args program arguments
     */
    public static CommandLineHelper parse(String[] args) {
        return new CommandLineHelper(args);
    }

    /**
     * 如果 args 中为空, 则抛出异常信息 `program arguments ia black`
     *
     * @param args jvm 启动参数
     */
    public static void requireNonEmpty(String[] args) {
        requireNonEmpty(args, null);
    }

    /**
     * 如果 args 中为空, 则抛出异常信息 msg, msg 默认为 `program arguments ia black`
     *
     * @param args jvm 启动参数
     * @param msg  如果 args 为空抛出的信息
     */
    public static void requireNonEmpty(String[] args, String msg) {
        // args 有值则直接跳出
        if (args != null && args.length > 1) {
            return;
        }
        // 如果 msg 有值
        if (msg == null || msg.trim().length() == 0) {
            msg = "program arguments ia black";
        }
        throw new IllegalArgumentException(msg);
    }

    /**
     * 获取参数信息
     * <p>
     * eg program arguments 参数中有 `-d fjk`, 则 getParam("d"), 返回值为 fjk
     *
     * @param key 参数key
     * @return 对应的参数值
     */
    public String getParam(String key) {
        if (keyParams == null) {
            return "";
        }
        return keyParams.get(key.toLowerCase());
    }

    /**
     * 获取参数信息, 如果查找不到则返回 defaultVal
     * <p>
     * eg program arguments 参数中有 `-d fjk`, 则 getParam("d"), 返回值为 fjk
     *
     * @param key        参数key
     * @param defaultVal 如果 program arguments 中没有 key, 则返回默认值
     * @return 对应的参数值
     */
    public String getDefaultParam(String key, String defaultVal) {
        final String keyVal = getParam(key);
        return keyVal.isEmpty() ? defaultVal : keyVal;
    }

    /**
     * 获取参数信息, 如果查找不到则返回 defaultVal
     * <p>
     * eg program arguments 参数中有 `-d fjk`, 则 getParam("d"), 返回值为 fjk
     *
     * @param key        参数key
     * @param defaultVal 如果 program arguments 中没有 key, 则返回默认值
     * @return 对应的参数值
     */
    public int getDefaultParam(String key, int defaultVal) {
        final String keyVal = getParam(key);
        return keyVal.isEmpty() ? defaultVal : Integer.parseInt(keyVal);
    }

    /**
     * 获取解析后的参数list
     */
    public List<String> getNoKeyParams() {
        if (noKeyParams == null) {
            noKeyParams = new ArrayList<>();
        }
        return noKeyParams;
    }

    /**
     * 获取解析后的参数map
     */
    public Map<String, String> getKeyParams() {
        if (keyParams == null) {
            keyParams = new HashMap<>();
        }
        return keyParams;
    }

    @Override
    public String toString() {
        return "CommandLineHelper{" +
                "noKeyParams=" + noKeyParams +
                ", keyParams=" + keyParams +
                '}';
    }
}
