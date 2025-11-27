package tech.challenge.scheduling.system.presentation.dtos.consultation;

import java.time.LocalDateTime;

public record ConsultationHistoryRequestDTO(
    Long patientId,
    Long doctorId,
    Long nurseId,
    LocalDateTime dateTime,
    String description,
    String notes
) {}