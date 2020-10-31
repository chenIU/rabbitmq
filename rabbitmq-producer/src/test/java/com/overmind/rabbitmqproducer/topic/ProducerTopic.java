package com.overmind.rabbitmqproducer.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 通配符工作模式
 * # 可以匹配一个或多个词
 * * 只能匹配一个词
 * @author chenjy
 * @since 2020/10/31 13:43
 */
public class ProducerTopic {

    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";

    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";

    private static final String EXCHANGE_TOPIC_INFORM = "exchange_topic_inform";

    public static final String ROUTINGKEY_EMAIL = "inform.#.email.#";

    public static final String ROUTINGKEY_SMS = "inform.#.sms.#";

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
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);

            //声明一个交换机
            //参数 String exchange, String type
            /**
             * 参数明显
             * 1、exchange：交换机名称
             * 2、type：交换机类型
             * fanout：对应publish/subscribe工作模式
             * direct：对应routing工作模式
             * topic：对应topic工作模式
             * headers：对应headers工作模式
             */
            channel.exchangeDeclare(EXCHANGE_TOPIC_INFORM, BuiltinExchangeType.TOPIC);

            //进行队列和交换机绑定
            //参数 String queue, String exchange, String routingKey
            /**
             * 参数说明
             * 1、queue：队列名称
             * 2、exchange：交换机名称
             * 3、routingKey：路由key，路由模式设置成指定的routingKey
             */
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_TOPIC_INFORM,ROUTINGKEY_EMAIL);
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_TOPIC_INFORM,ROUTINGKEY_SMS);

            //发送消息
            //参数 String exchange, String routingKey, BasicProperties props, byte[] body
            /**
             * 参数说明
             * 1、exchange：交换机，如果不指定将使用MQ提供的默认交换机
             * 2、routingKey：路由key，交换机根据routingKey将消息发送到不同的队列。如果使用默认交换机，routingKey使用队列名称
             * 3、props：消息属性
             * 4、body：消息体
             */

            //发送邮件通知
            for (int i = 0; i < 5; i++) {
                String message = "send email inform message to user:"+i;
                channel.basicPublish(EXCHANGE_TOPIC_INFORM,"inform.email",null,message.getBytes());
                System.out.println("send message to MQ:"+message);
            }

            //发送短信通知
            for (int i = 0; i < 5; i++) {
                String message = "send sms inform message to user:"+i;
                channel.basicPublish(EXCHANGE_TOPIC_INFORM,"inform.sms",null,message.getBytes());
                System.out.println("send message to MQ:"+message);
            }

            //发送邮件和短信
            for (int i = 0; i < 5; i++) {
                String message = "send sms and email inform message to user:"+i;
                channel.basicPublish(EXCHANGE_TOPIC_INFORM,"inform.sms.email",null,message.getBytes());
                System.out.println("send message to MQ:"+message);
            }
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
