package com.github.cosycode.common.util.common;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Description : </b> 字符串扩展处理类
 *
 * @author CPF
 * @since 1.0
 * @date 2019/10/10 11:48
 **/
public class StrUtils {

    private StrUtils() {
    }

    /**
     * 正则替换, 按照 regex 对 content 进行查询指定字符串, 并在字符串前后增加 prefix 前缀和 suffix 后缀
     * eg: replaceJoinAll("\\d+", "sing34hj32kh423jk", "<", ">");
     * return sing<34>hj<32>kh<42322>jk
     *
     * @param regex 正则表达式
     * @param content 文本
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 处理过的字符串
     */
    public static String replaceJoinAll(String regex, String content, String prefix, String suffix){
        final Pattern p = Pattern.compile(regex);
        // 获取 matcher 对象
        final Matcher m = p.matcher(content);
        final StringBuffer sb = new StringBuffer();
        while(m.find()){
            final String group = m.group();
            m.appendReplacement(sb, "");
            sb.append(prefix).append(group).append(suffix);
        }
        m.appendTail(sb);
        return sb.toString();
    }


    /**
     * 首字母变小写
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
     * @param string 待处理的字符串
     * @return 返回驼峰式字符串, 以'_'为分隔符
     */
    public static String lowerCamel(@NonNull String string){
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
     * @param string 待处理的字符串
     * @return 待处理字符串的小写下滑线形式, 以'_'为分隔符
     */
    public static String lowerDownLine(@NonNull String string){
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
     * @param string 待处理的字符串
     * @return 返回驼峰式字符串, 以'_'为分隔符
     */
    public static String upperCamel(@NonNull String string){
        return firstCharToUpperCase(lowerCamel(string));
    }

    /**
     * 返回待处理字符串的大写下滑线形式
     * @param string 待处理的字符串
     * @return 待处理字符串的大写下滑线形式, 以'_'为分隔符
     */
    public static String upperDownLine(@NonNull String string){
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
     * @param s 原始字符串
     * @param defaultStr 默认字符串
     * @return 如果原始字符串为空, 则返回默认字符串
     */
    public static String defaultIfBlank(String s, String defaultStr) {
        if (StringUtils.isBlank(s)) {
            return defaultStr;
        }
        return s;
    }
}
