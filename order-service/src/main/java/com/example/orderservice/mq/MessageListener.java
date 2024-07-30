package com.example.orderservice.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MessageListener {
/*    @RabbitListener(
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
    }*/
}
