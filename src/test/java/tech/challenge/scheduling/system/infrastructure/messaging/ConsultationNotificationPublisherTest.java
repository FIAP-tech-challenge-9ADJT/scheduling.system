package tech.challenge.scheduling.system.infrastructure.messaging;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

class ConsultationNotificationPublisherTest {

    @Test
    void publish_sendsMessageToQueue() {
        RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        ConsultationNotificationPublisher publisher = new ConsultationNotificationPublisher(rabbitTemplate, "consultation.notifications");
        ConsultationNotificationMessage msg = new ConsultationNotificationMessage(1L, java.time.LocalDateTime.now(), 1L, "John", "john@example.com");

        publisher.publish(msg);

        Mockito.verify(rabbitTemplate).convertAndSend("consultation.notifications", msg);
    }
}

