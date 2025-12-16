package tech.challenge.scheduling.system.presentation.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.challenge.scheduling.system.application.services.ConsultationHistoryService;
import tech.challenge.scheduling.system.domain.entities.ConsultationHistory;
import tech.challenge.scheduling.system.domain.entities.NotificationStatus;
import tech.challenge.scheduling.system.infrastructure.messaging.ConsultationNotificationMessage;
import tech.challenge.scheduling.system.infrastructure.messaging.ConsultationNotificationPublisher;

import java.util.Optional;

@RestController
@RequestMapping("/notifications/test")
public class NotificationTestController {

    private static final Logger log = LoggerFactory.getLogger(NotificationTestController.class);

    private final ConsultationHistoryService consultationService;
    private final ConsultationNotificationPublisher publisher;

    public NotificationTestController(ConsultationHistoryService consultationService,
                                      ConsultationNotificationPublisher publisher) {
        this.consultationService = consultationService;
        this.publisher = publisher;
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> triggerNotification(@PathVariable Long id, @RequestParam(defaultValue = "300000") long delayMs) {
        Optional<ConsultationHistory> opt = consultationService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ConsultationHistory c = opt.get();

        if (c.getNotificationStatus() == NotificationStatus.SENT) {
            log.info("Consulta {} já notificada. Nenhuma ação necessária.", id);
            return ResponseEntity.ok(
                    String.format("Consulta %d já notificada. Nenhuma ação necessária.", id)
            );
        }

        ConsultationNotificationMessage message = new ConsultationNotificationMessage(
                c.getId(),
                c.getDateTime(),
                c.getPatientId(),
                null,
                null
        );
        message.setProcessAfter(java.time.LocalDateTime.now().plusNanos(delayMs * 1_000_000));
        publisher.publishWithDelay(message, delayMs);
        return ResponseEntity.accepted().body(
                String.format("Mensagem enviada para fila de delay para processamento futuro. ID: %d, Delay: %d ms", id, delayMs)
        );
    }
}
