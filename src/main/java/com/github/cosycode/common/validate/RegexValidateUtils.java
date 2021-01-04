package com.github.cosycode.common.validate;

import java.util.regex.Pattern;

/**
 * <b>Description : </b> 正则表达式工具类
 * <p>
 * <b>created in </b> 2019/8/2
 *
 * @author CPF
 * @since 1.0
 **/
public class RegexValidateUtils {

    private RegexValidateUtils() {
    }

    /**
     * yyyy-MM-dd
     */
    public static final String REGEX_DATE = "^\\d{4}-(0?[1-9]|1[0-2])-((0?[1-9])|((1|2)[0-9])|30|31)$";

    /**
     * hh:mm:ss
     */
    public static final String REGEX_TIME = "^((0?[1-9])|(1[0-9])|(2[0-3]))(:((0?[1-9])|([0-5][0-9]))){2}$";

    /**
     * yyyy-MM-dd hh:mm:ss
     */
    public static final String REGEX_DATE_TIME = REGEX_DATE.substring(0, REGEX_DATE.length() - 1) + " " + REGEX_TIME.substring(1);

    public static final String REGEX_EMAIL = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";

    public static final String REGEX_PHONE = "^1([345789])\\d{9}$";

    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    public static boolean isPhone(String phone) {
        return Pattern.matches(REGEX_PHONE, phone);
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
