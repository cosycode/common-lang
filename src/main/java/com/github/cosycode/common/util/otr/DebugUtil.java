package com.github.cosycode.common.util.otr;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <b>Description : </b> 调试工具类
 * <p>
 * <b>created in </b> 2021/3/19
 *
 * @author CPF
 * @since 1.2
 **/
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebugUtil {

    @Getter
    @Setter
    private static boolean throwErrFlag = false;

    public static void assertTrue(boolean express, String message, Object... objects) {
        if (express) {
            return;
        }
        if (throwErrFlag) {
            throw new RuntimeException(PrintTool.format(message, objects));
        } else {
            log.error(message, objects);
        }
    }

}
