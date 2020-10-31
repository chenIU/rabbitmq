package com.overmind.rabbitmqconsumer.mq;

import com.overmind.rabbitmqconsumer.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author chenjy
 * @since 2020/10/31 16:13
 */
@Component
public class ConsumerHandler {

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_INFORM_EMAIL})
    public void consume(String msg, Message message, Channel channel){
        System.out.println("receive message from MQ:"+msg);
    }
}
