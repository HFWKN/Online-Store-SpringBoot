package com.liubingqi.order.config;

import com.liubingqi.common.constants.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单服务 MQ 配置（消费者侧）
 */
@Configuration
public class OrderRabbitMqConfig {

    @Bean
    public TopicExchange seckillOrderExchange() {
        return new TopicExchange(MqConstants.SECKILL_ORDER_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange seckillOrderDeadExchange() {
        return new TopicExchange(MqConstants.SECKILL_ORDER_DEAD_EXCHANGE, true, false);
    }

    @Bean
    public Queue seckillOrderCreateQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", MqConstants.SECKILL_ORDER_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", MqConstants.SECKILL_ORDER_DEAD_ROUTING_KEY);
        return new Queue(MqConstants.SECKILL_ORDER_CREATE_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue seckillOrderDeadQueue() {
        return new Queue(MqConstants.SECKILL_ORDER_DEAD_QUEUE, true);
    }

    @Bean
    public Binding seckillOrderCreateBinding(Queue seckillOrderCreateQueue, TopicExchange seckillOrderExchange) {
        return BindingBuilder
                .bind(seckillOrderCreateQueue)
                .to(seckillOrderExchange)
                .with(MqConstants.SECKILL_ORDER_CREATE_ROUTING_KEY);
    }

    @Bean
    public Binding seckillOrderDeadBinding(Queue seckillOrderDeadQueue, TopicExchange seckillOrderDeadExchange) {
        return BindingBuilder
                .bind(seckillOrderDeadQueue)
                .to(seckillOrderDeadExchange)
                .with(MqConstants.SECKILL_ORDER_DEAD_ROUTING_KEY);
    }

    /**
     * 统一使用 JSON 消息体，避免 JDK 反序列化白名单问题。
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
