package com.zzsd.inolibi.common;

import jakarta.annotation.Resource;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * redisson 通用限流器
 */
@Service
public class RedissonRateLimiter {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流方法
     */
    public void rateLimiter(String key) {
        // 1. 根据 key 获取（或创建）限流器对象
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);

        // 2. 初始化限流规则（仅在首次创建时生效）
        // 参数含义：OVERALL 全局限流，每秒生成 5 个令牌，时间窗口为 1 秒
        // 实际使用时请根据业务 QPS 调整这些数值
        rateLimiter.trySetRate(RateType.OVERALL, 5, 1, RateIntervalUnit.SECONDS);

        // 3. 尝试获取 1 个令牌
        boolean acquired = rateLimiter.tryAcquire(1);

        // 4. 判断是否获取成功
        if (!acquired) {
            // 获取失败，说明达到限流阈值，抛出异常或返回降级信息
            throw new RuntimeException("请求过于频繁，请稍后重试");
        }
    }

}
