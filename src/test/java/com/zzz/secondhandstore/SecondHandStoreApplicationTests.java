package com.zzz.secondhandstore;

import com.alibaba.druid.pool.DruidDataSource;
import com.zzz.pojo.entity.User;
import com.zzz.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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
    void test2(){
        List<User> users = (List<User>) redisTemplate.opsForValue().get("users");
        users.forEach(user -> System.out.println(user));
    }

}
