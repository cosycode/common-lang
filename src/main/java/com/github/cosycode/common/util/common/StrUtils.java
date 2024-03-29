package com.github.cosycode.common.util.common;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Description : </b> 字符串扩展处理类
 * <p>
 * <b>created in </b> 2019/10/10
 *
 * @author CPF
 * @since 1.0
 **/
public class StrUtils {

    private StrUtils() {
    }

    /**
     * 正则替换, 按照 regex 对 content 进行查询指定字符串, 并在字符串前后增加 prefix 前缀和 suffix 后缀
     * <p>
     * eg: replaceJoinAll("\\d+", "sing34hj32kh423jk", "\{", "\}");
     * return sing{34}hj{32}kh{42322}jk
     * </p>
     *
     * @param regex   正则表达式
     * @param content 文本
     * @param prefix  前缀
     * @param suffix  后缀
     * @return 处理过的字符串
     */
    public static String replaceJoinAll(String regex, String content, String prefix, String suffix) {
        final Pattern p = Pattern.compile(regex);
        // 获取 matcher 对象
        final Matcher m = p.matcher(content);
        final StringBuffer sb = new StringBuffer();
        while (m.find()) {
            final String group = m.group();
            m.appendReplacement(sb, "");
            if (prefix != null) {
                sb.append(prefix);
            }
            sb.append(group);
            if (suffix != null) {
                sb.append(suffix);
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 正则替换, 按照 regex 对 content 进行对比字符串, 并按指定规则对字符串进行替换
     * <p>
     * eg: replaceAll("\\d+", "sing34hj32kh423jk", it -> "\{" + it + "\}");
     * return sing{34}hj{32}kh{42322}jk
     * </p>
     *
     * @param regex        正则表达式
     * @param content      文本
     * @param matchDispose 将匹配的字符串转换为目标字符串的方法
     * @return 处理过的字符串
     */
    public static String replaceAll(@NonNull String regex, @NonNull String content, @NonNull UnaryOperator<String> matchDispose) {
        final Pattern p = Pattern.compile(regex);
        // 获取 matcher 对象
        final Matcher m = p.matcher(content);
        final StringBuffer sb = new StringBuffer();
        while (m.find()) {
            final String group = m.group();
            m.appendReplacement(sb, "");
            sb.append(matchDispose.apply(group));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 正则查找, 查找在 content 里面所有符合 regex 条件的字符串, 并调用回调函数
     *
     * @param regex    正则表达式
     * @param content  文本
     * @param callback Predicate 回调函数: 若回调函数中 返回 false, 则表示跳出查找.
     */
    public static void findAllWithBreak(@NonNull String regex, @NonNull String content, @NonNull Predicate<String> callback) {
        final Pattern p = Pattern.compile(regex);
        // 获取 matcher 对象
        final Matcher m = p.matcher(content);
        while (m.find()) {
            final String group = m.group();
            if (!callback.test(group)) {
                break;
            }
        }
    }

    /**
     * 正则查找, 查找在 content 里面所有符合 regex 条件的字符串, 并调用回调函数
     *
     * @param regex    正则表达式
     * @param content  文本
     * @param callback 回调函数
     */
    public static void findAll(@NonNull String regex, @NonNull String content, @NonNull Consumer<String> callback) {
        findAllWithBreak(regex, content, match -> {
            callback.accept(match);
            return true;
        });
    }

    /**
     * 正则查找, 返回所有在 content 里面所有符合 regex 条件的字符串集合
     *
     * @param regex    正则表达式
     * @param content  文本
     */
    public static List<String> findAllMatch(@NonNull String regex, @NonNull String content) {
        List<String> list = new ArrayList<>();
        findAllWithBreak(regex, content, match -> {
            list.add(match);
            return true;
        });
        return list;
    }

    /**
     * 首字母变小写
     *
     * @param str 待转换的字符串
     * @return 转换后的字符串
     */
    public static String firstCharToLowerCase(@NonNull String str) {
        str = str.trim();
        if (str.isEmpty()) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 首字母变大写
     *
     * @param str 待转换的字符串
     * @return 转换后的字符串
     */
    public static String firstCharToUpperCase(@NonNull String str) {
        str = str.trim();
        if (str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 返回待处理字符串的驼峰格式
     *
     * @param string 待处理的字符串
     * @return 返回驼峰式字符串, 以'_'为分隔符
     */
    public static String lowerCamel(@NonNull String string) {
        if (!string.contains("_")) {
            return string.trim().toLowerCase();
        }
        string = string.trim();
        int len = string.length();
        StringBuilder sb = new StringBuilder(len);
        boolean nextUpper = false;
        for (int i = 0; i < len; i++) {
            char c = string.charAt(i);
            if (c == '_') {
                nextUpper = true;
            } else if (nextUpper) {
                sb.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    /**
     * 返回待处理字符串的小写下滑线形式
     *
     * @param string 待处理的字符串
     * @return 待处理字符串的小写下滑线形式, 以'_'为分隔符
     */
    public static String lowerDownLine(@NonNull String string) {
        string = string.trim();
        int len = string.length();
        if (len == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            final char ch = string.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * 返回待处理字符串的驼峰格式
     *
     * @param string 待处理的字符串
     * @return 返回驼峰式字符串, 以'_'为分隔符
     */
    public static String upperCamel(@NonNull String string) {
        return firstCharToUpperCase(lowerCamel(string));
    }

    /**
     * 返回待处理字符串的大写下滑线形式
     *
     * @param string 待处理的字符串
     * @return 待处理字符串的大写下滑线形式, 以'_'为分隔符
     */
    public static String upperDownLine(@NonNull String string) {
        string = string.trim();
        int len = string.length();
        if (len == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            final char ch = string.charAt(i);
            if (Character.isLowerCase(ch)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toUpperCase(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * @param s          原始字符串
     * @param defaultStr 默认字符串
     * @return 如果原始字符串为空, 则返回默认字符串
     */
    public static String defaultIfBlank(String s, String defaultStr) {
        if (StringUtils.isBlank(s)) {
            return defaultStr;
        }
        return s;
    }

    /**
     * 将String对象组连接起来, 如果为 null, 则转换为 EMPTY_STRING 连接
     *
     * @param strings string对象组
     * @return 连接后的字符串
     */
    public static String concat(@NonNull String... strings) {
        if (strings.length <= 1) {
            return strings[0];
        }
        StringBuilder sb = new StringBuilder();
        for (String str : strings) {
            sb.append(str);
        }
        return sb.toString();
    }

}
