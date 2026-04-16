package com.liubingqi.seckill.config;

import com.liubingqi.common.constants.MqConstants;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 秒杀服务 MQ 配置（生产者侧）
 */
@Configuration
public class SeckillRabbitMqConfig {

    /**
     * 声明交换机，确保生产者可直接发送。
     */
    @Bean
    public TopicExchange seckillOrderExchange() {
        return new TopicExchange(MqConstants.SECKILL_ORDER_EXCHANGE, true, false);
    }

    /**
     * 统一使用 JSON 消息体，避免 JDK 反序列化白名单问题。
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
