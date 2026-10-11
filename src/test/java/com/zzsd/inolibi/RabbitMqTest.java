package com.zzsd.inolibi;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * rabbitmq测试类
 */
@SpringBootTest
public class RabbitMqTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    void send (){
        String message = "hello world";
        String queueName = "bi.ai.queue";
        rabbitTemplate.convertAndSend(queueName,message);
    }


}
