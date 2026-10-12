package com.zzsd.inolibi.mq;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列的消息转换器配置类
 */
@Configuration
public class MessageConverterConfig {

    /**
     * 配置消息转换器
     * @return MessageConverter
     */
    @Bean
    public MessageConverter messageConverter(){
        JacksonJsonMessageConverter jjmc = new JacksonJsonMessageConverter();
        // 携带唯一消息id，确保消息幂等
        jjmc.setCreateMessageIds(true);
        return jjmc;
    }

}
