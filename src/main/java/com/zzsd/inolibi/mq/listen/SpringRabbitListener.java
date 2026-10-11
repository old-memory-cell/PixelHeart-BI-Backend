package com.zzsd.inolibi.mq.listen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * rabbitmq监听类
 */
@Slf4j
@Component
public class SpringRabbitListener {

    /**
     * 监听队列
     * @param msg 消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "bi.ai.queue", durable = "true"),
            exchange = @Exchange(value = "bi.ai.exchange.direct",type = ExchangeTypes.DIRECT),
            key = "ai.analiyze"
    ))
    public void listenBiAiQueue(String msg) {
        log.info("rabbitmq监听类，接收到的消息为：{}",msg);
    }


}
