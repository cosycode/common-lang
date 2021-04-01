package com.github.cosycode.common.util.otr;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/3/19
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
public class PrintTool {

    private static final String DIRECTOR_STRING = " ==> ";

    enum Level {
        OFF(Integer.MAX_VALUE), FATAL(50000), ERROR(40000), WARN(30000), INFO(30000), DEBUG(30000);

        private final int val;

        Level(int val) {
            this.val = val;
        }

        public int toInt() {
            return val;
        }

    }

    /**
     * 1: log, 0: 控制台
     */
    @Setter
    private static boolean logFlag;

    @Setter
    private static Level logLevel = Level.DEBUG;

    public static void printDebug(String s, Object... objects) {
        print(Level.DEBUG, s, objects);
    }

    public static void printError(String s, Object... objects) {
        print(Level.ERROR, s, objects);
    }

    public static void printWarning(String s, Object... objects) {
        print(Level.WARN, s, objects);
    }

    public static void printInfo(String s, Object... objects) {
        print(Level.INFO, s, objects);
    }

    public static void printSuccess(String s, Object... objects) {
        print(Level.INFO, s, objects);
    }

    @SuppressWarnings("java:S106")
    public static void print(Level level, String s, Object... objects) {
        switch (level) {
            case DEBUG:
                if (logLevel.toInt() <= Level.DEBUG.toInt()) {
                    if (logFlag) {
                        log.debug(s, objects);
                    } else {
                        System.out.println(level.name() + DIRECTOR_STRING + format(s, objects));
                    }
                }
                break;
            case INFO:
                if (logLevel.toInt() <= Level.INFO.toInt()) {
                    if (logFlag) {
                        log.info(s, objects);
                    } else {
                        System.out.println(level.name() + DIRECTOR_STRING + format(s, objects));
                    }
                }
                break;
            case WARN:
                if (logLevel.toInt() <= Level.WARN.toInt()) {
                    if (logFlag) {
                        log.warn(s, objects);
                    } else {
                        System.out.println(level.name() + DIRECTOR_STRING + format(s, objects));
                    }
                }
                break;
            case ERROR:
                if (logLevel.toInt() <= Level.ERROR.toInt()) {
                    if (logFlag) {
                        log.error(s, objects);
                    } else {
                        System.out.println(level.name() + DIRECTOR_STRING + format(s, objects));
                    }
                }
                break;
            default:
        }
    }

    public static String format(String str, Object... objects) {
        if (objects == null || objects.length == 0) {
            return str;
        }
        int from = 0;
        int end;
        StringBuilder sb = new StringBuilder();
        for (Object object : objects) {
            end = str.indexOf("{}", from);
            if (end < 0) {
                break;
            }
            sb.append(str, from, end);
            sb.append(object);
            from = end + 2;
        }
        return sb.toString();
    }

}
