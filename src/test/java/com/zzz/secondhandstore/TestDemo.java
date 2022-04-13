package com.zzz.secondhandstore;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Date;
import java.util.concurrent.*;

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

    @Test
    void test4(){

        Boolean result = false;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        //使用Callable接口作为构造参数
        FutureTask<Boolean> future = new FutureTask<>(() -> {
            //真正的任务在这里执行，这里的返回值类型为String，可以为任意类型
                System.out.println("执行中...");
                return true;
        });
        //在这里可以做别的任何事情
        try {
            executor.execute(future);
            result = future.get(100, TimeUnit.MILLISECONDS); //取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果
        } catch (InterruptedException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
            future.cancel(true);
        } catch (TimeoutException e) {
            e.printStackTrace();
            future.cancel(true);
        } finally {
            executor.shutdown();
        }

        System.out.println(result);
    }



}
