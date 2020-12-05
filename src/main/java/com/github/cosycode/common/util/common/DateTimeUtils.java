package com.github.cosycode.common.util.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2020/6/22 14:10
 */
public class DateTimeUtils {

    public static Date castDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public LocalDateTime castDateToDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
