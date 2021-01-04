package com.github.cosycode.common.util.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * <b>Description : </b> 日期时间工具类
 * <p>
 * <b>created in </b> 2020/6/22
 *
 * @author CPF
 * @since 1.0
 */
public class DateTimeUtils {

    public static Date castDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public LocalDateTime castDateToDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
