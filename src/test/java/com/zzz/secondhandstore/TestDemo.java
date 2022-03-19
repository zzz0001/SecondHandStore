package com.zzz.secondhandstore;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Date;

/**
 * @author zzz
 * @date 2022/3/1 16:17
 */
public class TestDemo {

    @Test
    void test(){
        System.out.println(SecureUtil.md5("123"));
    }

    @Test
    void test1(){
        StringBuffer stringBuffer = new StringBuffer();
        for (int i=1;i<5;i++){
            stringBuffer.append(i+"zzz-");
        }
        String ss = "D:/TestFile/1646234603995.png-D:/TestFile/1646234604040.jpg-D:/TestFile/1646234604090.png-";
        String[] strings = ss.split("-");
        for (String str : strings){
            System.out.println(str);
        }
//        System.out.println(stringBuffer.toString());
    }


    @Test
    void test2(){
        String goodsImg = "D:/TestFile/1646298930045.png-";
        String[] strings = goodsImg.split("-");
        for (String str : strings) {
            boolean delete = new File(str).delete();
            if (!delete){
                System.out.println("删除失败");
            }
        }
    }

    @Test
    void test3(){
        Date date = new Date("2021/3/15");
        System.out.println(date);
        int age = DateUtil.ageOfNow(date);
        System.out.println(age);
    }
}
