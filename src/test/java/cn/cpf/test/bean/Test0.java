package cn.cpf.test.bean;

import com.github.cosycode.common.base.RunnableWithThrow;
import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.common.util.otr.TestUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.function.Consumer;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/2
 *
 * @author CPF
 * @since
 **/
@Slf4j
public class Test0 {

    public static void main(String[] args) {

        // java 这门语言, 在1.8版本时叫java, 在1.8之前只能叫辣鸡.
        // java 开发,我真的离不开lambda表达式和函数式接口了
        TestUtils.callTime(() -> {
            Runnable test2 = null;
            for (int i = 0; i < 1000000000; i++) {
                test2 = Test0::test2;
            }
            System.out.println(test2);
        });

        TestUtils.callTime(() -> {
            RunnableWithThrow<Exception> test2 = null;
            for (int i = 0; i < 1000000000; i++) {
                test2 = Test0::test2;
            }
            System.out.println(test2);
        });
    }

    private static void test2() {

    }

    /**
     * 获取一个byte[]的md5值(可处理大文件)
     *
     * @param data 待转换的数据
     * @return md5 value 文件的MD5值
     */
    public static String getMD5(byte[] data) {
        final MessageDigest md5 = Throws.fun("MD5", MessageDigest::getInstance).runtimeExp().value();
        byte[] digest = md5.digest(data);
        return new BigInteger(1, digest).toString(16);
    }

}
