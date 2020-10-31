package com.overmind.rabbitmqproducer.wq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 工作队列模式
 * @author chenjy
 * @since 2020/10/31 13:43
 */
public class ProducerWorkQueue {

    private static final String QUEUE_NAME = "demo";

    public static void main(String[] args) {

        //创建连接工厂和MQ建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.127.133");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("chenjy");
        connectionFactory.setPassword("123456");
        //设置虚拟机，一个MQ可以设置多个虚拟机，每个虚拟机相当于一个MQ
        connectionFactory.setVirtualHost("/");

        Connection connection = null;

        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();

            //创建会话通道，生产者和MQ所有的通信都在通道中完成
            channel = connection.createChannel();

            //声明队列(如果队列在MQ中没有，则要创建)
            //参数 String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            /**
             * 参数说明
             * 1、queue：队列名称
             * 2、durable：是否持久化，重启后队列还在
             * 3、exclusive：是否排他(是否独占连接，队列只允许在该连接中访问，连接关闭后，队列删除)
             * 4、autoDelete：是否自动删除
             * 5、arguments：扩展参数
             */
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);

            //发送消息
            //参数 String exchange, String routingKey, BasicProperties props, byte[] body
            /**
             * 参数说明
             * 1、exchange：交换机，如果不指定将使用MQ提供的默认交换机
             * 2、routingKey：路由key，交换机根据routingKey将消息发送到不同的队列。如果使用默认交换机，routingKey使用队列名称
             * 3、props：消息属性
             * 4、body：消息体
             */
            String message = "hello 陈俭银";
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            System.out.println("send message to MQ:"+message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            //先关闭通道，再关闭连接(channel在connection里)
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
