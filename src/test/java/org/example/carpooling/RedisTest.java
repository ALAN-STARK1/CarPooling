package org.example.carpooling;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void add() {
        String key = "testKey";      // 自己定义
        String value = "testValue";   // 自己定义

        stringRedisTemplate.opsForValue().set(key, value);
        System.out.println("添加成功");
    }

    @Test
    public void get() {
        String key = "testKey";
        String value = stringRedisTemplate.opsForValue().get(key);
        System.out.println("获取到的值: " + value);
    }

}
