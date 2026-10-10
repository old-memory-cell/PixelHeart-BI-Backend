package com.zzsd.inolibi.common;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 限流器测试
 */
@SpringBootTest
class RedissonRateLimiterTest {

    @Resource
    private RedissonRateLimiter redissonRateLimiter;

    /**
     * 测试限流方法
     */
    @Test
    void rateLimiter() throws InterruptedException {
        String userId = "123";
        for (int i = 0; i < 2; i++) {
            redissonRateLimiter.rateLimiter(userId);
            System.out.println("成功" + i + "次");
        }
        Thread.sleep(1000);
        for (int i = 0; i < 5; i++) {
            redissonRateLimiter.rateLimiter(userId);
            System.out.println("成功" + i + "次");
        }

    }
}