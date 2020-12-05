package com.github.cosycode.common.util.common;

import org.apache.commons.lang3.Validate;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2020/5/14 18:25
 */
public class MathUtils {

    private MathUtils(){}

    public static BigInteger factorial(int n){
        Validate.isTrue(n >= 0, "factorial() n不能小于0");
        if (n==0){
            return BigInteger.valueOf(1);
        }
        Optional<BigInteger> reduce = Arrays.stream(ArrUtils.getIntervalArr(1, n, 1)).mapToObj(BigInteger::valueOf).reduce(BigInteger::multiply);
        return reduce.orElse(BigInteger.valueOf(1));
    }

}
