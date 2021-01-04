package cn.cpf.test;

import com.github.cosycode.common.ext.hub.LazySingleton;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @date 2020/12/17 16:51
 **/
@Slf4j
public class SingleTonTest {

    private SingleTonTest(){}

    public static final LazySingleton<Object> singleton1 = LazySingleton.of(Object::new);

    public static final LazySingleton<BlockingQueue<String>> singleton2 = LazySingleton.of(() -> {
        log.info("创建一个ArrayBlockingQueue, 大小为10000");
        return new ArrayBlockingQueue<>(10000);
    });

    public static final LazySingleton<Map<String, Object>> singleton3 = LazySingleton.of(() -> {
        log.info("创建一个ArrayBlockingQueue, 大小为10000");
        return new HashMap<>(100);
    });

    public static final LazySingleton<Robot> robotLazySingleton = LazySingleton.of(() -> {
        log.info("创建一个Robot 实例对象, 大小为10000");
        try {
            return new Robot();
        } catch (AWTException e) {
            log.info("创建一个Robot 实例对象, 创建失败");
            return null;
        }
    });

    public static void main(String[] args) {
        log.debug("singleton1: {}", singleton1.instance());
        log.debug("singleton1: {}", singleton1.instance());
        log.debug("singleton1: {}\n", singleton1.instance());

        log.debug("singleton2: {}", singleton2.instance().hashCode());
        log.debug("singleton2: {}", singleton2.instance().hashCode());
        log.debug("singleton2: {}\n", singleton2.instance().hashCode());

        log.debug("singleton3: {}", singleton3.instance().hashCode());
        log.debug("singleton3: {}", singleton3.instance().hashCode());
        log.debug("singleton3: {}\n", singleton3.instance().hashCode());

        log.debug("robotLazySingleton: {}", robotLazySingleton.instance().hashCode());
        log.debug("robotLazySingleton: {}", robotLazySingleton.instance().hashCode());
        log.debug("robotLazySingleton: {}\n", robotLazySingleton.instance().hashCode());
    }

}
