package com.example.orderservice.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.example.commom.constant.RabbitMQConstant.*;

@Configuration
@Slf4j
public class MessageListener {
    @RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                    value = SEND_MESSAGE_QUEUE_1
                ),
                exchange = @Exchange(value = DEMO_MESSAGE_EXCHANGE,type = ExchangeTypes.DIRECT),
                key = DEMO_MESSAGE_SEND_KEY
        ),
            ackMode = "MANUAL"
    )
    @Transactional
    public void acceptMessage(String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.info("消费队列获取到消息:[{}]", message);
        //表示消费成功
        channel.basicAck(deliveryTag, false);
    }
}
