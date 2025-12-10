package tech.challenge.scheduling.system.infrastructure.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsultationNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    public ConsultationNotificationPublisher(RabbitTemplate rabbitTemplate,
                                             @Value("${notifications.queue:consultation.notifications}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    public void publish(ConsultationNotificationMessage message) {
        rabbitTemplate.convertAndSend(queueName, message);
    }
}

