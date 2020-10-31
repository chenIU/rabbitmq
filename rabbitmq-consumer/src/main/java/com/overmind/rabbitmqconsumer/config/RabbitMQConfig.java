package com.overmind.rabbitmqconsumer.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenjy
 * @since 2020/10/31 15:53
 */
@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";

    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";

    public static final String EXCHANGE_TOPIC_INFORM = "exchange_topic_inform";

    public static final String ROUTINGKEY_EMAIL = "inform.#.email.#";

    public static final String ROUTINGKEY_SMS = "inform.#.sms.#";

    //声明交换机
    @Bean(EXCHANGE_TOPIC_INFORM)
    public Exchange topicExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPIC_INFORM).durable(true).build();
    }

    //声明队列
    @Bean(QUEUE_INFORM_EMAIL)
    public Queue emailQueue(){
        return new Queue(QUEUE_INFORM_EMAIL);
    }

    @Bean(QUEUE_INFORM_SMS)
    public Queue smsQueue(){
        return new Queue(QUEUE_INFORM_SMS);
    }

    //绑定交换机和队列
    @Bean
    public Binding BindEmail(@Qualifier(QUEUE_INFORM_EMAIL) Queue queue,
                             @Qualifier(EXCHANGE_TOPIC_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_EMAIL).noargs();
    }
}
