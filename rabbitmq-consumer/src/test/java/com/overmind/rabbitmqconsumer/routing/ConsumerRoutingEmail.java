package com.overmind.rabbitmqconsumer.routing;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author chenjy
 * @since 2020/10/31 14:07
 */
public class ConsumerRoutingEmail {

    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";

    private static final String EXCHANGE_DIRECT_INFORM = "exchange_direct_inform";

    public static final String ROUTINGKEY_EMAIL = "inform_mail";

    public static void main(String[] args) {
        //创建连接工厂和MQ建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.127.133");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("chenjy");
        connectionFactory.setPassword("123456");
        //设置虚拟机，一个MQ可以设置多个虚拟机，每个虚拟机相当于一个MQ
        connectionFactory.setVirtualHost("/");

        Connection connection;
        try {
            connection = connectionFactory.newConnection();

            Channel channel = connection.createChannel();

            //声明队列(防止生产者没有启动)
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);

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
            channel.exchangeDeclare(EXCHANGE_DIRECT_INFORM, BuiltinExchangeType.DIRECT);

            //进行队列和交换机绑定
            //参数 String queue, String exchange, String routingKey
            /**
             * 参数说明
             * 1、queue：队列名称
             * 2、exchange：交换机名称
             * 3、routingKey：路由key，在发布订阅模式中设置为空字符串
             */
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_DIRECT_INFORM,ROUTINGKEY_EMAIL);

            //实现消费方法
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel){

                //当接受到消息之后，此方法被调用

                /**
                 *
                 * @param consumerTag 消费者标签，用来标识消费者
                 * @param envelope 信封，通过此参数可以获取关于MQ的信息
                 * @param properties 消息的属性
                 * @param body 消息内容
                 * @throws IOException
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //super.handleDelivery(consumerTag, envelope, properties, body);

                    //交换机
                    String exchange = envelope.getExchange();

                    //消息id，MQ在channel中用来表示消息的id，可用于确认消息已接收
                    long deliveryTag = envelope.getDeliveryTag();

                    String message = new String(body,"utf-8");
                    System.out.println("receive message from MQ:"+message);
                }
            };

            //消费
            //参数 String queue, boolean autoAck, Consumer callback
            /**
             * 参数说明
             * 1、queue：队列名称
             * 2、autoAck：自动确认，当消费者收到消息之后要向MQ确认，true表示会自动回复MQ，如果设置为false则需要手动实现
             * 3、callback：消费方法
             */
            channel.basicConsume(QUEUE_INFORM_EMAIL,true,defaultConsumer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //消费端不需要关闭连接，一直监听
    }
}
