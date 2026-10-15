package com.zzsd.inolibi.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的配置类
 */
@Configuration
public class ThreadPoolExecutorConfig {

    /**
     * 线程池配置
     *
     * @return 线程池
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        // 手动创建线程工厂
        ThreadFactory threadFactory = r -> {
            Thread thread = new Thread(r);
            return thread;
        };
        // 返回线程池
        return new ThreadPoolExecutor(
                10,
                20,
                100,
                TimeUnit.SECONDS, // 线程空闲时间单位
                new ArrayBlockingQueue<>(1000), // 阻塞队列
                threadFactory, // 线程工厂
                new ThreadPoolExecutor.AbortPolicy() // 拒绝策略
        );
    }

}
