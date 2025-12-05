package tech.challenge.scheduling.system.presentation.dtos.graphql;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record CreateConsultationInputDTO(
    @NotNull(message = "Field 'patientId' not must be null") Long patientId,
    @NotNull@NotNull(message = "Field 'doctorId' not must be null") Long doctorId,
    Long nurseId,
    @NotBlank(message = "Field 'dateTime' not must be blank or null") String dateTime,
    @NotBlank(message = "Field 'description' not must be blank or null") String description,
    String notes
) {}
