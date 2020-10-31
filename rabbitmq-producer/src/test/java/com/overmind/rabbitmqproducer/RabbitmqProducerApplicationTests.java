package com.overmind.rabbitmqproducer;

import com.overmind.rabbitmqproducer.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;

@SpringBootTest
class RabbitmqProducerApplicationTests {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    public void sendEmail(){

        String message = "send email message to user";

        /**
         * 1、交换机
         * 2、routingKey
         * 3、消息内容
         */
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_TOPIC_INFORM,"inform.email",message);
    }

}
