package tech.challenge.scheduling.system.infrastructure.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.MessagePostProcessor;

@Component
public class ConsultationNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;
    private final String delayExchange;
    private final String delayRoutingKey;

    public ConsultationNotificationPublisher(RabbitTemplate rabbitTemplate,
                                             @Value("${notifications.exchange:consultation.notifications.exchange}") String exchange,
                                             @Value("${notifications.routing-key:notification.consultation}") String routingKey,
                                             @Value("${notifications.delay.exchange:consultation.notifications.delay.exchange}") String delayExchange,
                                             @Value("${notifications.delay.routing-key:notification.consultation.delay}") String delayRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.delayExchange = delayExchange;
        this.delayRoutingKey = delayRoutingKey;
    }

    public void publish(ConsultationNotificationMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    public void publishWithDelay(ConsultationNotificationMessage message, long delayMs) {
        MessagePostProcessor mpp = msg -> {
            msg.getMessageProperties().setExpiration(String.valueOf(delayMs));
            return msg;
        };
        rabbitTemplate.convertAndSend(delayExchange, delayRoutingKey, message, mpp);
    }
}
