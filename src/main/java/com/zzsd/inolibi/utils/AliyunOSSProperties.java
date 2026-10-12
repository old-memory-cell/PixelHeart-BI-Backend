package com.zzsd.inolibi.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOSSProperties {
    /*
    *当配置文件中的属性很多时，依旧会冗余和复用差，所以将配置文件属性抽取到单独的类中
    * 通过@ConfigrationProperties注解获取
    * */
    private String endpoint;
    private String bucketName;
    private String region;
}
