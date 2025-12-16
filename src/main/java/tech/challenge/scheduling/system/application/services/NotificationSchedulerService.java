package tech.challenge.scheduling.system.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.MessagePostProcessor;
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
    private final String exchange;
    private final String routingKey;
    private final String delayExchange;
    private final String delayRoutingKey;
    private final String delayQueue;
    private final String scheduleCron;

    public NotificationSchedulerService(ConsultationHistoryRepository repository,
                                        RabbitTemplate rabbitTemplate,
                                        @Value("${notifications.queue:consultation.notifications}") String queueName,
                                        @Value("${notifications.exchange:consultation.notifications.exchange}") String exchange,
                                        @Value("${notifications.routing-key:notification.consultation}") String routingKey,
                                        @Value("${notifications.delay.exchange:consultation.notifications.delay.exchange}") String delayExchange,
                                        @Value("${notifications.delay.routing-key:notification.consultation.delay}") String delayRoutingKey,
                                        @Value("${notifications.delay.queue:consultation.notifications.delay}") String delayQueue,
                                        @Value("${notifications.cron:0 0 8 * * *}") String scheduleCron) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.delayExchange = delayExchange;
        this.delayRoutingKey = delayRoutingKey;
        this.delayQueue = delayQueue;
        this.scheduleCron = scheduleCron;
    }

    @Scheduled(cron = "${notifications.cron:0 0 8 * * *}")
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

    @RabbitListener(queues = "${notifications.queue:consultation.notifications}")
    @Transactional
    public void consumeNotification(ConsultationNotificationMessage message) {
        log.info("Consumed message for consultation {} at {} to patient {}",
                message.getConsultationId(),
                message.getDateTime(),
                message.getPatientEmail());

        try {
            if (message.getProcessAfter() != null && java.time.LocalDateTime.now().isBefore(message.getProcessAfter())) {
                long remainingMs = java.time.Duration.between(java.time.LocalDateTime.now(), message.getProcessAfter()).toMillis();
                MessagePostProcessor mpp = msg -> {
                    msg.getMessageProperties().setExpiration(String.valueOf(Math.max(remainingMs, 1000)));
                    return msg;
                };
                rabbitTemplate.convertAndSend(delayExchange, delayRoutingKey, message, mpp);
                return;
            }
            ConsultationHistory consultation = repository.findById(message.getConsultationId()).orElse(null);
            if (consultation == null) {
                rabbitTemplate.convertAndSend(delayExchange, delayRoutingKey, message);
                return;
            }

            if (consultation.getNotificationStatus() == NotificationStatus.SENT) {
                return;
            }

            boolean isTomorrow = consultation.getDateTime().toLocalDate().equals(LocalDate.now().plusDays(1));
            if (!isTomorrow) {
                rabbitTemplate.convertAndSend(delayExchange, delayRoutingKey, message);
                return;
            }

            int maxAttempts = 3;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                consultation.setNotificationAttempts(consultation.getNotificationAttempts() + 1);
                consultation.setNotificationStatus(NotificationStatus.SENT);
                consultation.setNotificationSentAt(LocalDateTime.now());
                repository.save(consultation);
                log.info("Notification sent to {} for consultation {} (attempt {})", message.getPatientEmail(), message.getConsultationId(), attempt);
                return;
            }

            consultation.setNotificationStatus(NotificationStatus.FAILED);
            repository.save(consultation);
            rabbitTemplate.convertAndSend(delayExchange, delayRoutingKey, message);
            log.error("Notification failed for consultation {} - requeued to delay", message.getConsultationId());
        } catch (Exception e) {
            log.error("Error processing message for consultation {}", message.getConsultationId(), e);
            rabbitTemplate.convertAndSend(delayExchange, delayRoutingKey, message);
        }
    }

    @Scheduled(fixedDelayString = "${notifications.delay.poll-ms:30000}")
    public void pollDelayQueue() {
        Object obj;
        while ((obj = rabbitTemplate.receiveAndConvert(delayQueue)) != null) {
            ConsultationNotificationMessage message = (ConsultationNotificationMessage) obj;
            log.info("Polled from delay queue: consultation {} scheduled after {}", message.getConsultationId(), message.getProcessAfter());
            consumeNotification(message);
        }
    }
}
