package com.zzz.secondhandstore;

import com.alibaba.druid.pool.DruidDataSource;
import com.zzz.pojo.entity.Goods;
import com.zzz.pojo.entity.User;
import com.zzz.service.GoodsService;
import com.zzz.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest
class SecondHandStoreApplicationTests {

    @Resource
    private DruidDataSource druidDataSource;

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private GoodsService goodsService;


    @Test
    void testVersion(){
        Goods goods = goodsService.getById(2);
        goods.setGoodsInventory(5);

        Goods goods1 = goodsService.getById(2);
        goods1.setGoodsInventory(9);
        goodsService.updateById(goods1);
        System.out.println("第二个更新成功");

        goodsService.updateById(goods);
        System.out.println("更新成功");
    }


    @Test
    void contextLoads() throws SQLException {
        System.out.println(druidDataSource.getMaxActive());
        System.out.println(druidDataSource.getInitialSize());
        System.out.println(druidDataSource.getMinIdle());
        System.out.println(druidDataSource.getMaxWait());
    }

    @Test
    void test1(){
        List<User> list = userService.list();
        redisTemplate.opsForValue().set("users",list);
    }

    @Test
    void test11(){
        redisTemplate.opsForValue().set("ABC","AAA");
    }

    @Test
    void test12(){
        redisTemplate.boundListOps("list").rightPush("中国");
        redisTemplate.boundListOps("list").rightPush("B");
        redisTemplate.boundListOps("list").rightPush("C");
    }

    @Test
    void test2(){
        List<User> users = (List<User>) redisTemplate.opsForValue().get("users");
        users.forEach(user -> System.out.println(user));
    }


    @Test
    void test3(){
        BoundListOperations user = redisTemplate.boundListOps("user");
        user.leftPush("A");
        user.leftPush("B");
        user.leftPush("C");
        System.out.println("添加成功");
    }
    @Test
    void test4(){
        BoundListOperations user = redisTemplate.boundListOps("user");
        List range = user.range(0, -1);
        range.forEach(System.out::println);
        redisTemplate.delete("user");
        System.out.println("删除成功");
    }

    @Test
    void test5(){
        Boolean hasKey = redisTemplate.hasKey("user");
        System.out.println(hasKey);
    }
}
