package cn.cpf.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/1/4
 *
 * @author CPF
 **/
public class Trtrrt {

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("fdfd");
        FileInputStream fileInputStream = new FileInputStream(file);
        System.out.println(fileInputStream);
    }

}
