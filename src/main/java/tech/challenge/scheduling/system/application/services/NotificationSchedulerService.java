package tech.challenge.scheduling.system.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.scheduling.system.domain.entities.ConsultationHistory;
import tech.challenge.scheduling.system.domain.entities.NotificationStatus;
import tech.challenge.scheduling.system.infrastructure.messaging.ConsultationNotificationMessage;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.ConsultationHistoryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class NotificationSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(NotificationSchedulerService.class);

    private final ConsultationHistoryRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    public NotificationSchedulerService(ConsultationHistoryRepository repository,
                                        RabbitTemplate rabbitTemplate,
                                        @Value("${notifications.queue:consultation.notifications}") String queueName) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void scheduleTomorrowNotifications() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime start = LocalDateTime.of(tomorrow, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(tomorrow, LocalTime.MAX);
        List<ConsultationHistory> consultations = repository.findTomorrowPendingNotifications(start, end, NotificationStatus.SENT);

        for (ConsultationHistory c : consultations) {
            try {
                c.setNotificationStatus(NotificationStatus.SCHEDULED);
                repository.save(c);
                log.info("Notification scheduled for consultation {} on {}", c.getId(), c.getDateTime());
            } catch (Exception e) {
                log.error("Error scheduling notification for consultation {}", c.getId(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void processQueueForTomorrow() {
        while (true) {
            Object obj = rabbitTemplate.receiveAndConvert(queueName);
            if (obj == null) break;
            ConsultationNotificationMessage message = (ConsultationNotificationMessage) obj;

            log.info("Consumed message from queue '{}' for consultation {} at {} to patient {}",
                    queueName,
                    message.getConsultationId(),
                    message.getDateTime(),
                    message.getPatientEmail());

            try {
                ConsultationHistory consultation = repository.findById(message.getConsultationId()).orElse(null);
                if (consultation == null) continue;

                if (consultation.getNotificationStatus() == NotificationStatus.SENT) {
                    continue;
                }

                boolean isTomorrow = message.getDateTime().toLocalDate().equals(LocalDate.now().plusDays(1));
                if (!isTomorrow) {
                    rabbitTemplate.convertAndSend(queueName, message);
                    continue;
                }

                int maxAttempts = 3;
                boolean sent = false;
                for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                    consultation.setNotificationAttempts(consultation.getNotificationAttempts() + 1);
                    consultation.setNotificationStatus(NotificationStatus.SENT);
                    consultation.setNotificationSentAt(LocalDateTime.now());
                    repository.save(consultation);
                    log.info("Notification sent to {} for consultation {} (attempt {})", message.getPatientEmail(), message.getConsultationId(), attempt);
                    sent = true;
                    break;
                }

                if (!sent) {
                    consultation.setNotificationStatus(NotificationStatus.FAILED);
                    repository.save(consultation);
                    rabbitTemplate.convertAndSend(queueName, message);
                    log.error("Notification failed for consultation {} - requeued", message.getConsultationId());
                }
            } catch (Exception e) {
                log.error("Error processing message for consultation {}", message.getConsultationId(), e);
                rabbitTemplate.convertAndSend(queueName, message);
            }
        }
    }
}
