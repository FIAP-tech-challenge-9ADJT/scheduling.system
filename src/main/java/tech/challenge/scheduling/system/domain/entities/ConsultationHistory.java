package tech.challenge.scheduling.system.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import tech.challenge.scheduling.system.domain.entities.NotificationStatus;

@Entity
@Table(name = "consultation_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long patientId;

	@Column(nullable = false)
	private Long doctorId;

	private Long nurseId;

	@Column(nullable = false)
	private LocalDateTime dateTime;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(columnDefinition = "TEXT")
	private String notes;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ConsultationStatus status = ConsultationStatus.SCHEDULED;

	@Enumerated(EnumType.STRING)
	@Column(name = "notification_status")
	private NotificationStatus notificationStatus;

	@Column(name = "notification_sent_at")
	private LocalDateTime notificationSentAt;

	@Column(name = "notification_attempts", nullable = false)
	private int notificationAttempts = 0;
}
