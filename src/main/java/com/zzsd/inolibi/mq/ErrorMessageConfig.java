package com.zzsd.inolibi.mq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息消费失败的配置类
 */
@Configuration
@RequiredArgsConstructor
public class ErrorMessageConfig {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 配置交换机
     * @return DirectExchange
     */
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("error.direct");
    }

    /**
     * 配置队列
     * @return Queue
     */
    @Bean
    public Queue errorQueue(){
        return new Queue("error.queue");
    }

    /**
     * 绑定队列和交换机
     * @return 绑定关系
     */
    @Bean
    public Binding exchange(Queue errorQueue ,DirectExchange directExchange){
        return BindingBuilder.bind(errorQueue).to(directExchange).with("error");
    }

    /**
     * 消息消费失败的处理
     * @return MessageRecoverer
     */
    @Bean
    public MessageRecoverer messageRecoverer(){
        return new RepublishMessageRecoverer(rabbitTemplate , "error.direct" , "error");
    }

}
