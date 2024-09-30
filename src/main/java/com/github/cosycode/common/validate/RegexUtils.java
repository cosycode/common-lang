package com.github.cosycode.common.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Description : </b> 正则表达式工具类
 * <p>
 * <b>created in </b> 2019/8/2
 *
 * @author CPF
 * @since 1.0
 **/
public class RegexUtils {

    private RegexUtils() {
    }

    /**
     * yyyy-MM-dd
     */
    public static final String REGEX_DATE = "\\d{4}-(0?[1-9]|1[0-2])-((0?[1-9])|((1|2)[0-9])|30|31)";

    /**
     * ipv4 地址
     */
    public static final String REGEX_IPV4 = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";

    /**
     * 汉字
     */
    public static final String REGEX_CHINESE_CHARACTER = "[\\u4e00-\\u9fa5]";

    /**
     * hh:mm:ss
     */
    public static final String REGEX_TIME = "((0?[1-9])|(1[0-9])|(2[0-3]))(:((0?[1-9])|([0-5][0-9]))){2}";

    /**
     * yyyy-MM-dd hh:mm:ss
     */
    public static final String REGEX_DATE_TIME = REGEX_DATE.substring(0, REGEX_DATE.length() - 1) + " " + REGEX_TIME.substring(1);

    public static final String REGEX_EMAIL = "[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}";

    public static final String REGEX_PHONE = "1([345789])\\d{9}";

    public static boolean isRegex(String regexString, String content) {
        return Pattern.matches("^" + regexString + "$", content);
    }

    /**
     * 根据正则表达式, 匹配 content 里面的一段字符串, 并返回该字符串
     *
     * @param regex 正则表达式
     * @param content 匹配内容
     * @return 匹配成功的第一个表达式
     */
    public static String matchFirst(String regex, String content) {
        final Pattern p = Pattern.compile(regex);
        // 获取 matcher 对象
        final Matcher m = p.matcher(content);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

    /**
     * 根据正则表达式, 匹配 content 里面的字符串, 返回匹配成功的第 n 个字符串.
     *
     * @param regex 正则表达式
     * @param content 匹配内容
     * @return 匹配成功的第 n 个表达式(注意: n 从 1 开始, n > 1)
     */
    public static String matchTheNrd(String regex, String content, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n cannot less than 0");
        }
        final Pattern p = Pattern.compile(regex);
        // 获取 matcher 对象
        final Matcher m = p.matcher(content);
        while (m.find()) {
            final String group = m.group();
            n --;
            if (n <= 0) {
                return group;
            }
        }
        return null;
    }

    public static boolean isEmail(String email) {
        return isRegex(REGEX_EMAIL, email);
    }

    public static boolean isPhone(String phone) {
        return isRegex(REGEX_PHONE, phone);
    }

    public static boolean isDigit(String string) {
        for (int i = 0, len = string.length(); i < len; i++) {
            if (!Character.isDigit(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }


}
