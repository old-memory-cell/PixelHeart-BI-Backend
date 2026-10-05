package com.zzsd.inolibi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 项目启动类
 */
@MapperScan("com.zzsd.inolibi.mapper")
@SpringBootApplication
public class InoliBiApplication {

    public static void main(String[] args) {
        SpringApplication.run(InoliBiApplication.class, args);
    }

}
