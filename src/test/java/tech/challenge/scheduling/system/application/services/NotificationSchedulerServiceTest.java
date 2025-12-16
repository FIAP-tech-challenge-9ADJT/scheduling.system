package tech.challenge.scheduling.system.application.services;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tech.challenge.scheduling.system.domain.entities.ConsultationHistory;
import tech.challenge.scheduling.system.domain.entities.NotificationStatus;
import tech.challenge.scheduling.system.infrastructure.messaging.ConsultationNotificationMessage;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.ConsultationHistoryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationSchedulerServiceTest {

    @Test
    void scheduleTomorrowNotifications_marksScheduled() {
        ConsultationHistoryRepository repo = Mockito.mock(ConsultationHistoryRepository.class);
        RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        NotificationSchedulerService service = new NotificationSchedulerService(
                repo, rabbitTemplate,
                "consultation.notifications",
                "consultation.notifications.exchange",
                "notification.consultation",
                "consultation.notifications.delay.exchange",
                "notification.consultation.delay",
                "consultation.notifications.delay",
                "0 0 8 * * *"
        );

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        ConsultationHistory c = new ConsultationHistory();
        c.setId(10L);
        c.setDateTime(LocalDateTime.of(tomorrow, java.time.LocalTime.NOON));
        Mockito.when(repo.findTomorrowPendingNotifications(Mockito.any(), Mockito.any(), Mockito.eq(NotificationStatus.SENT)))
               .thenReturn(List.of(c));

        service.scheduleTomorrowNotifications();

        ArgumentCaptor<ConsultationHistory> captor = ArgumentCaptor.forClass(ConsultationHistory.class);
        Mockito.verify(repo).save(captor.capture());
        assertEquals(NotificationStatus.SCHEDULED, captor.getValue().getNotificationStatus());
    }

    @Test
    void consumeNotification_updatesSent() {
        ConsultationHistoryRepository repo = Mockito.mock(ConsultationHistoryRepository.class);
        RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        NotificationSchedulerService service = new NotificationSchedulerService(
                repo, rabbitTemplate,
                "consultation.notifications",
                "consultation.notifications.exchange",
                "notification.consultation",
                "consultation.notifications.delay.exchange",
                "notification.consultation.delay",
                "consultation.notifications.delay",
                "0 0 8 * * *"
        );

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        ConsultationHistory c = new ConsultationHistory();
        c.setId(10L);
        c.setDateTime(LocalDateTime.of(tomorrow, java.time.LocalTime.NOON));
        Mockito.when(repo.findById(10L)).thenReturn(java.util.Optional.of(c));

        ConsultationNotificationMessage msg = new ConsultationNotificationMessage(10L, c.getDateTime(), 1L, "John", "john@example.com");
        service.consumeNotification(msg);

        Mockito.verify(repo, Mockito.times(1)).save(Mockito.argThat(saved -> saved.getNotificationStatus() == NotificationStatus.SENT));
    }
}
