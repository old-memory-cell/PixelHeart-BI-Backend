package com.zzsd.inolibi.config;

import com.zzsd.inolibi.common.RedisProperties;
import jakarta.annotation.Resource;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * redisson 配置
 */
@Component
@Configuration
public class RedissonClientConfig {

    @Resource
    private RedisProperties redisProperties;


    /**
     * redisson 客户端
     *
     * @return RedissonClient
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());
        return Redisson.create(config);
    }

}
