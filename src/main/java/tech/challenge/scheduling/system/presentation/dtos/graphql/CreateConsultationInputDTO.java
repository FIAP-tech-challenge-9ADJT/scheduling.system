package tech.challenge.scheduling.system.presentation.dtos.graphql;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record CreateConsultationInputDTO(
    @NotNull Long patientId,
    @NotNull Long doctorId,
    Long nurseId,
    @NotBlank String dateTime,
    @NotBlank String description,
    String notes
) {}
