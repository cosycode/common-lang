package cn.cpf.test.bean;

import com.github.cosycode.common.util.common.BeanUtils;
import com.github.cosycode.common.util.otr.PrintTool;
import com.github.cosycode.common.util.otr.TestUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * <b>Description : </b>
 *
 * @author CPF
 **/
@Slf4j
public class BeanUtilsTest {

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        for (int i = 11110; i > 0; i-=30) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
