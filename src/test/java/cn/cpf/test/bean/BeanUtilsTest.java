package cn.cpf.test.bean;

import com.github.cosycode.common.util.common.BeanUtils;
import com.github.cosycode.common.util.otr.TestUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * <b>Description : </b>
 *
 * @author CPF
 **/
public class BeanUtilsTest {

    public static void main(String[] args) {
        Bean1 bean1 = new Bean1();
        bean1.setStr1("str11");
        bean1.setStr2("str22");
        bean1.setBool1(true);
        bean1.setBool2(true);
        bean1.setFloat1(0.1F);
        bean1.setFloat2(4.4F);
        bean1.setDouble1(4.0D);
        bean1.setDouble2(5.0D);
        bean1.setByte1((byte)6);
        bean1.setByte2((byte)5);
        bean1.setShort1((short)7);
        bean1.setShort2((short)98);
        bean1.setInt1(2345);
        bean1.setInt2(354);
        bean1.setLong1(3420L);
        bean1.setLong2(34230L);
        bean1.setCh1('t');
        bean1.setCh2('t');
        System.out.println(bean1);

        final Map<String, Object> stringObjectMap = BeanUtils.pojoToStringObjectMap(bean1);
        System.out.println(stringObjectMap);
        TestUtils.callTime("tet", () -> {
            BeanUtils.copyProperties(new Bean2(), bean1);
        });

        System.out.println(System.getProperty("file.encoding"));

    }

}
