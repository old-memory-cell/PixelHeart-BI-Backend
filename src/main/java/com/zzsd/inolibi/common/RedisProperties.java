package com.zzsd.inolibi.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 读取 redis 的 yml文件配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    /**
     * redis地址
     */
    private String host;

    /**
     * redis端口
     */
    private Integer port;

}
