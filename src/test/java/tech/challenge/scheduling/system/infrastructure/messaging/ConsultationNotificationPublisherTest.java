package tech.challenge.scheduling.system.infrastructure.messaging;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.MessagePostProcessor;

class ConsultationNotificationPublisherTest {

    @Test
    void publish_sendsMessageToQueue() {
        RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        ConsultationNotificationPublisher publisher = new ConsultationNotificationPublisher(
                rabbitTemplate,
                "consultation.notifications.exchange",
                "notification.consultation",
                "consultation.notifications.delay.exchange",
                "notification.consultation.delay"
        );
        ConsultationNotificationMessage msg = new ConsultationNotificationMessage(1L, java.time.LocalDateTime.now(), 1L, "John", "john@example.com");

        publisher.publish(msg);

        Mockito.verify(rabbitTemplate).convertAndSend("consultation.notifications.exchange", "notification.consultation", msg);
    }

    @Test
    void publishWithDelay_sendsMessageToDelayExchange() {
        RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        ConsultationNotificationPublisher publisher = new ConsultationNotificationPublisher(
                rabbitTemplate,
                "consultation.notifications.exchange",
                "notification.consultation",
                "consultation.notifications.delay.exchange",
                "notification.consultation.delay"
        );
        ConsultationNotificationMessage msg = new ConsultationNotificationMessage(2L, java.time.LocalDateTime.now(), 2L, "Alice", "alice@example.com");

        publisher.publishWithDelay(msg, 60000);

        Mockito.verify(rabbitTemplate).convertAndSend(Mockito.eq("consultation.notifications.delay.exchange"), Mockito.eq("notification.consultation.delay"), Mockito.eq(msg), Mockito.any(MessagePostProcessor.class));
    }
}
